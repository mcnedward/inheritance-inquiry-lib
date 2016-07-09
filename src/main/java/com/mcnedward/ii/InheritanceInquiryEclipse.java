package com.mcnedward.ii;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jgit.util.FileUtils;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.jdt.visitor.ClassVisitor;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.utils.ASTUtils;
import com.mcnedward.ii.utils.Stopwatch;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InheritanceInquiryEclipse extends FileASTRequestor {
	private static final Logger logger = Logger.getLogger(InheritanceInquiryEclipse.class);

	private Sourcer mSourcer;
	private List<CompilationUnitHolder> mHolders;

	public InheritanceInquiryEclipse() {
		mSourcer = new Sourcer();
		mHolders = new ArrayList<>();
	}

	public void buildProject(String projectPath, String projectName, ProjectBuildListener listener) {
		Runnable task = () -> {
			listener.onProgressChange(String.format("Starting to load %s...", projectName), 0);
			JavaProject project = new JavaProject(projectPath, projectName);
			build(project, false, listener);
			if (project != null)
				listener.finished(project);
		};

		Thread thread = new Thread(task);
		thread.start();
	}

	private JavaProject build(JavaProject project, boolean deleteAfterBuild, ProjectBuildListener listener) {
		Stopwatch.start();
		try {
			// Get all the files for the project
			List<SourcedFile> files = mSourcer.buildSourceForProject(project, listener);
			
			createASTs(project.getProjectFile().getAbsolutePath(), files);
			visitCompilationUnits(project, files, listener);

			String timeToComplete = Stopwatch.stopAndGetTime();
			System.out.println("FINISHED! Time to complete: " + timeToComplete);
			listener.onProgressChange(String.format("Finished! Time to complete: %s", timeToComplete), 100);
			return project;
		} catch (IOException e) {
			listener.onProgressChange(String.format("Something went wrong loading the file %s.", project.getProjectFile()), 100);
			logger.error(String.format("Something went wrong loading the file %s.", project.getProjectFile()), e);
			return null;
		} finally {
			if (deleteAfterBuild) {
				try {
					FileUtils.delete(project.getProjectFile(), FileUtils.RECURSIVE | FileUtils.RETRY);
					logger.info(String.format("Deleting %s", project.getProjectFile().getName()));
				} catch (IOException e) {
					try {
						FileUtils.delete(project.getProjectFile(), FileUtils.RECURSIVE | FileUtils.RETRY);
						logger.warn(String.format("Could not delete file: %s... Trying one more time.", project.getProjectFile()));
					} catch (IOException e1) {
						logger.error(String.format("Could not delete file: %s after second attempt...", project.getProjectFile()), e);
					}
				}
			}
		}
	}
	
	/**
	 * Setup the ASTParser with the correct options, source files, classPaths, and environment, then creates the ASTs from a batch of {@link CompilationUnit}s.
	 * @param projectPath The absolute path of the project.
	 * @param files The list of {@link SourcedFile}s.
	 */
	private void createASTs(String projectPath, List<SourcedFile> files) {
		@SuppressWarnings("unchecked")
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setCompilerOptions(options);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		
		String[] classPaths = ASTUtils.classPathEntries();
		String[] sources = ASTUtils.sourceEntries(projectPath);
		String[] encodings = ASTUtils.encodings();
		parser.setEnvironment(classPaths, sources, encodings, true);

		List<String> filePaths = new ArrayList<>();
		for (SourcedFile file : files) {
			filePaths.add(file.getFile().getAbsolutePath());
		}
		String[] sourceFiles = filePaths.toArray(new String[filePaths.size()]); 
		parser.createASTs(sourceFiles, null, new String[0], this, null);
	}
	
	@Override
	public void acceptAST(String sourceFilePath, CompilationUnit compilationUnit) {
		CompilationUnitHolder holder = new CompilationUnitHolder();
		holder.cu = compilationUnit;
		holder.sourceFilePath = sourceFilePath;
		mHolders.add(holder);
	}
	
	/**
	 * Checks the {@link CompilationUnit}s for problems, then visits each of them.
	 * @param project The {@link JavaProject}.
	 * @param files The list of {@link SourcedFile}s.
	 * @param listener The {@link ProjectBuildListener}.
	 */
	private void visitCompilationUnits(JavaProject project, List<SourcedFile> files, ProjectBuildListener listener) {
		for (int i = 0; i < mHolders.size(); i++) {
			CompilationUnitHolder holder = mHolders.get(i);
			
			int progress = (int) (((double) i / mHolders.size()) * 100);
			listener.onProgressChange("Analyzing...", progress);
			
			String fileName = holder.sourceFilePath;
			for (SourcedFile f : files) {
				if (f.getFile().getAbsolutePath().equals(holder.sourceFilePath)) {
					fileName = f.getName();
					break;
				}
			}
			CompilationUnit cu = holder.cu;
			
			List<String> missingImports = new ArrayList<>();
			IProblem[] problems = cu.getProblems();
			if (problems != null && problems.length > 0) {
		        logger.debug(String.format("Got %s problems compiling the source file: %s", problems.length, fileName));
		        for (IProblem problem : problems) {
		            logger.debug(String.format("%s", problem));
		            if (problem.getMessage().contains("import")) {
		            	missingImports.add(problem.getArguments()[0]);	// This is assuming a lot I think, but should be right...
		            }
		        }
		    }
			
			cu.accept(new ClassVisitor(project, fileName, missingImports));
		}
	}
	
	private final class CompilationUnitHolder {
		public CompilationUnit cu;
		public String sourceFilePath;
	}
	
}
