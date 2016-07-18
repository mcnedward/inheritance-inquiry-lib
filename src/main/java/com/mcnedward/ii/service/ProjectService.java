package com.mcnedward.ii.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jgit.util.FileUtils;

import com.google.common.base.Stopwatch;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.jdt.visitor.ClassVisitor;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.utils.ASTUtils;
import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.SourcedFile;
import com.mcnedward.ii.utils.Sourcer;

/**
 * This service is used to build a {@link JavaProject}. This takes a file for a project directory, or path to a project
 * directory, the parses all the .java files to strings. Next, it builds {@link CompilationUnit}s for all of those
 * files. Then it visits all of those CompilationUnits to build the project.
 * 
 * @author Edward - Jun 16, 2016
 *
 */
public class ProjectService extends FileASTRequestor {
	private static final Logger logger = Logger.getLogger(ProjectService.class);

	private Sourcer mSourcer;
	private List<CompilationUnitHolder> mHolders;

	public ProjectService() {
		mSourcer = new Sourcer();
		mHolders = new ArrayList<>();
	}

	/**
	 * Build a {@link JavaProject}.
	 * 
	 * @param projectPath
	 *            The path to the project
	 * @param projectName
	 *            The name to give the project
	 * @param listener
	 *            The {@link ProjectBuildListener}
	 * @return The JavaProject
	 */
	private JavaProject build(String projectPath, String projectName, ProjectBuildListener listener) {
		JavaProject project = new JavaProject(projectPath, projectName);
		buildProject(project, listener);
		return project;
	}

	/**
	 * Build a {@link JavaProject}. This method should be used when building a project that is a part of system (for
	 * projects with multiple versions).
	 * 
	 * @param projectFile
	 *            The project file
	 * @param systemName
	 *            The name of the system that this project belongs to.
	 * @return The JavaProject
	 */
	public JavaProject build(File projectFile, String systemName) {
		return build(projectFile, systemName, null);
	}

	/**
	 * Build a {@link JavaProject}. This method should be used when building a project that is a part of system (for
	 * projects with multiple versions).
	 * 
	 * @param projectFile
	 *            The project file
	 * @param systemName
	 *            The name of the system that this project belongs to.
	 * @param listener
	 *            The {@link ProjectBuildListener}
	 * @return The JavaProject
	 */
	public JavaProject build(File projectFile, String systemName, ProjectBuildListener listener) {
		JavaProject project = new JavaProject(projectFile, systemName);
		buildProject(project, listener);
		return project;
	}

	private void buildProject(JavaProject project, ProjectBuildListener listener) {
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			// Get all the files for the project
			List<SourcedFile> files = mSourcer.buildSourceForProject(project, listener);

			// Create the Abstract Syntax Trees. The build listener for this is not perfect, so it will show 0% until
			// all ASTs are created, when it will then show 100% before moving to the next task.
			// TODO Maybe use IProgressMonitor with SubMonitor for this?
			// https://eclipse.org/articles/Article-Progress-Monitors/article.html
			if (listener != null)
				listener.onProgressChange("Creating ASTs...", 0);
			else
				IILogger.debug("Creating ASTs for %s...", project.toString());

			createASTs(project.getProjectFile().getAbsolutePath(), files);
			if (listener != null)
				listener.onProgressChange("Finished creating ASTs!", 100);
			else
				IILogger.debug("Finished creating ASTs for %s!", project.toString());

			// Use the visitors to build the JavaProject!
			visitCompilationUnits(project, files, listener);

			afterBuild(project);

			stopwatch.stop();
			String timeToComplete = stopwatch.toString();
			if (listener != null)
				listener.onProgressChange(String.format("Finished! Time to complete: %s", timeToComplete), 100);
			else
				IILogger.debug("Finished building %s! Time to complete: %s", project.toString(), timeToComplete);

			if (project != null && listener != null)
				listener.finished(project);

		} catch (IOException e) {
			if (listener != null)
				listener.onBuildError(String.format("Something went wrong loading the file %s.", project.getProjectFile()), e);
			logger.error(String.format("Something went wrong loading the file %s.", project.getProjectFile()), e);
		}
	}

	/**
	 * Build a {@link JavaProject}. This can also delete the project file after the build is complete. Useful when using
	 * the {@link GitService}, and you need to cleanup the cloned project.
	 * 
	 * @param project
	 *            The JavaProject
	 * @param listener
	 *            The {@link ProjectBuildListener}
	 * @param deleteAfterBuild
	 *            True if the project file should be deleted after the build is complete
	 */
	public void build(String projectPath, String projectName, ProjectBuildListener listener, boolean deleteAfterBuild) {
		JavaProject project = build(projectPath, projectName, listener);
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

	private void afterBuild(JavaProject project) {
		// AnalyzerUtils.calculateExtendedMethods(project);
	}

	/**
	 * Setup the ASTParser with the correct options, source files, classPaths, and environment, then creates the ASTs
	 * from a batch of {@link CompilationUnit}s.
	 * 
	 * @param projectPath
	 *            The absolute path of the project.
	 * @param files
	 *            The list of {@link SourcedFile}s.
	 * @param listener
	 *            The ProjectBuildListener
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
		parser.createASTs(sourceFiles, null, new String[0], this, getProgressMonitor());
		parser = null;
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
	 * 
	 * @param project
	 *            The {@link JavaProject}.
	 * @param files
	 *            The list of {@link SourcedFile}s.
	 * @param listener
	 *            The {@link ProjectBuildListener}.
	 */
	private void visitCompilationUnits(JavaProject project, List<SourcedFile> files, ProjectBuildListener listener) {
		for (int i = 0; i < mHolders.size(); i++) {
			CompilationUnitHolder holder = mHolders.get(i);

			int progress = (int) (((double) i / mHolders.size()) * 100);
			if (listener != null)
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
						// This is assuming a lot I think, but should be right...
						missingImports.add(problem.getArguments()[0]);
					}
				}
			}

			cu.accept(new ClassVisitor(project, fileName, missingImports));
		}
	}

	private static final IProgressMonitor getProgressMonitor() {
		return new IProgressMonitor() {
			private String taskName;
			private boolean cancelled;

			@Override
			public void worked(int arg0) {
				logger.debug(arg0);
			}

			@Override
			public void subTask(String arg0) {
			}

			@Override
			public void setTaskName(String arg0) {
				taskName = arg0;
			}

			@Override
			public void setCanceled(boolean arg0) {
				cancelled = arg0;
			}

			@Override
			public boolean isCanceled() {
				return cancelled;
			}

			@Override
			public void internalWorked(double arg0) {
			}

			@Override
			public void done() {
				logger.debug(taskName);
			}

			@Override
			public void beginTask(String arg0, int arg1) {
				logger.debug(taskName + " - " + arg0 + " - " + arg1);
			}
		};
	}

	private final class CompilationUnitHolder {
		public CompilationUnit cu;
		public String sourceFilePath;
	}

}
