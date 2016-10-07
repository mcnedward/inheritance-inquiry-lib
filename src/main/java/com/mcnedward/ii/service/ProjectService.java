package com.mcnedward.ii.service;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.exception.ProjectBuildException;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.jdt.visitor.ClassVisitor;
import com.mcnedward.ii.listener.SolutionBuildListener;
import com.mcnedward.ii.utils.ASTUtils;
import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.SourcedFile;
import com.mcnedward.ii.utils.Sourcer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jgit.annotations.Nullable;
import org.eclipse.jgit.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * This service is used to build a {@link JavaProject}. This takes a file for a project directory, or path to a project
 * directory, the parses all the .java files to strings. Next, it builds {@link CompilationUnit}s for all of those
 * files. Then it visits all of those CompilationUnits to build the project.
 * 
 * @author Edward - Jun 16, 2016
 *
 */
public final class ProjectService {

	private Sourcer mSourcer;
	private List<CompilationUnitHolder> mHolders;
	private IIFileASTRequester mRequester;
	private boolean mDeleteAfterBuild;

	public ProjectService() {
		mSourcer = new Sourcer();
		mHolders = new ArrayList<>();
		mRequester = new IIFileASTRequester();
	}

	/**
	 * Build a {@link JavaProject}.
	 * 
	 * @param projectPath
	 *            The path to the project.
	 * @param listener
	 *            The {@link SolutionBuildListener}. This can be null.
	 * @return The JavaProject.
	 */
	public JavaProject build(String projectPath, @Nullable SolutionBuildListener listener) throws ProjectBuildException {
		JavaProject project = new JavaProject(projectPath);
		return buildProject(project, listener);
	}

	/**
	 * Build a {@link JavaProject}.
	 * 
	 * @param projectFile
	 *            The project file.
	 * @param listener
	 *            The {@link SolutionBuildListener}. This can be null.
	 * @return The JavaProject.
	 */
	public JavaProject build(File projectFile, @Nullable SolutionBuildListener listener) throws ProjectBuildException {
		JavaProject project = new JavaProject(projectFile);
		return buildProject(project, listener);
	}

	public JavaProject build(File projectFile, String projectName, @Nullable SolutionBuildListener listener) throws ProjectBuildException {
        JavaProject project = new JavaProject(projectFile, projectName);
        return buildProject(project, listener);
    }

	private JavaProject buildProject(JavaProject project, SolutionBuildListener listener) throws ProjectBuildException {
		try {
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

			createASTs(project.getProjectFile().getAbsolutePath(), files, listener);
			if (listener != null)
				listener.onProgressChange("Finished creating ASTs!", 100);
			else
				IILogger.debug("Finished creating ASTs for %s!", project.toString());

			// Use the visitors to build the JavaProject!
			visitCompilationUnits(project, files, listener);

            if (listener != null)
                listener.onProgressChange("Finished building project!", 100);

			afterBuild(project);
			mHolders.clear();
		} catch (IOException | IllegalStateException e) {
            throw new ProjectBuildException(String.format("Something went wrong loading the file %s.\nPlease check that the source files of your project are in the correct directory.", project.getProjectFile()), e);
		}

		if (mDeleteAfterBuild) {
			try {
				FileUtils.delete(project.getProjectFile(), FileUtils.RECURSIVE | FileUtils.RETRY);
				IILogger.info(String.format("Deleting %s", project.getProjectFile().getName()));
			} catch (IOException e) {
				try {
					FileUtils.delete(project.getProjectFile(), FileUtils.RECURSIVE | FileUtils.RETRY);
					IILogger.info(String.format("Could not delete file: %s... Trying one more time.", project.getProjectFile()));
				} catch (IOException e1) {
					IILogger.error(String.format("Could not delete file: %s after second attempt...", project.getProjectFile()), e);
				}
			}
		}
		return project;
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
	 * @throws TaskBuildException
	 */
	private void createASTs(String projectPath, List<SourcedFile> files, SolutionBuildListener listener) throws ProjectBuildException {
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
		String[] encodings = ASTUtils.encodings(sources.length);
		parser.setEnvironment(classPaths, sources, encodings, true);

		List<String> filePaths = new ArrayList<>();
		for (SourcedFile file : files) {
			filePaths.add(file.getFile().getAbsolutePath());
		}
		String[] sourceFiles = filePaths.toArray(new String[filePaths.size()]);
		parser.createASTs(sourceFiles, null, new String[0], mRequester, getProgressMonitor(listener, sourceFiles.length));
	}

	/**
	 * Checks the {@link CompilationUnit}s for problems, then visits each of them.
	 * 
	 * @param project
	 *            The {@link JavaProject}.
	 * @param files
	 *            The list of {@link SourcedFile}s.
	 * @param listener
	 *            The {@link SolutionBuildListener}.
	 */
	private void visitCompilationUnits(JavaProject project, List<SourcedFile> files, SolutionBuildListener listener) {
		for (int i = 0; i < mHolders.size(); i++) {
			CompilationUnitHolder holder = mHolders.get(i);

			String fileName = holder.sourceFilePath;
			for (SourcedFile f : files) {
				if (f.getFile().getAbsolutePath().equals(holder.sourceFilePath)) {
					fileName = f.getName();
					break;
				}
			}
			CompilationUnit cu = holder.cu;

            int progress = (int) (((double) i / mHolders.size()) * 100);
            IILogger.notify(listener, String.format("Visiting compilation units [%s / %s]...", i, mHolders.size()), progress);

            List<String> missingImports = new ArrayList<>();
			IProblem[] problems = cu.getProblems();
			if (problems != null && problems.length > 0) {
				IILogger.debug(String.format("Got %s problems compiling the source file: %s", problems.length, fileName));
				for (IProblem problem : problems) {
					IILogger.debug(String.format("%s", problem));
					if (problem.getMessage().contains("import")) {
						// This is assuming a lot I think, but should be right...
						missingImports.add(problem.getArguments()[0]);
					}
				}
			}

			cu.accept(new ClassVisitor(project, fileName, missingImports));
		}
	}

	private static final IProgressMonitor getProgressMonitor(SolutionBuildListener listener, int projectFileCount) {
		return new IProgressMonitor() {
            private boolean mCancelled;
            private int mWorkedProgress;

			@Override
			public void worked(int arg0) {
                mWorkedProgress += arg0;
                    int progress = (int) (((double) mWorkedProgress / projectFileCount) * 100);
                    listener.onProgressChange("Creating ASTs...", progress);
			}

			@Override
			public void subTask(String arg0) {
			}

			@Override
			public void setTaskName(String arg0) {
			}

			@Override
			public void setCanceled(boolean arg0) {
				mCancelled = arg0;
			}

			@Override
			public boolean isCanceled() {
				return mCancelled;
			}

			@Override
			public void internalWorked(double arg0) {
			}

			@Override
			public void done() {
			}

			@Override
			public void beginTask(String arg0, int arg1) {
			}
		};
	}

	/**
	 * Determines whether the project should be deleted after the build process is complete. Be careful with this! This
	 * is mainly used when doing a build for a file from the {@link GitService} and you need to cleanup the cloned
	 * project.
	 * 
	 * @param deleteAfterBuild
	 *            True if you want the entire project to be deleted after the build process, false otherwise.
	 */
	public void setDeleteAfterBuild(boolean deleteAfterBuild) {
		mDeleteAfterBuild = deleteAfterBuild;
	}

	private final class CompilationUnitHolder {
		public CompilationUnit cu;
		public String sourceFilePath;
	}

	private class IIFileASTRequester extends FileASTRequestor {

		@Override
		public void acceptAST(String sourceFilePath, CompilationUnit compilationUnit) {
			CompilationUnitHolder holder = new CompilationUnitHolder();
			holder.cu = compilationUnit;
			holder.sourceFilePath = sourceFilePath;
			mHolders.add(holder);
		}
	}

}