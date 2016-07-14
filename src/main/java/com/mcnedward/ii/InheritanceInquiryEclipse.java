package com.mcnedward.ii;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jgit.util.FileUtils;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.jdt.visitor.ClassVisitor;
import com.mcnedward.ii.listener.BuildTaskListener;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.listener.SystemBuildListener;
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

	// Tasks
	private static final int TIMEOUT = 5000;
	private static final int MAX_TIMEOUTS = 20;	// One minute of timeouts

	public InheritanceInquiryEclipse() {
		mSourcer = new Sourcer();
		mHolders = new ArrayList<>();
	}
	
	public void buildSystem(String systemPath, ExecutorService executor, BlockingQueue<Runnable> queue, int taskQueueSize, SystemBuildListener listener) {
		File systemFile = new File(systemPath);
		if (systemFile.isFile()) {
			listener.onBuildError(String.format("You need to provide a directory for a system! [Path: %s]", systemPath), null);
			return;
		}
		JavaSystem system = new JavaSystem(systemFile);

		File[] projects = system.getFiles();
		int projectCount = projects.length;
		
		for (int i = 0; i < projectCount; i++) {
			File projectFile = projects[i];
			
			executor.submit(() -> {
				runSystemBuildTask(system, projectFile, new BuildTaskListener() {
					@Override
					public void onProgressChange(String message, int progress) {
						listener.onProgressChange(message, progress);
					}

					@Override
					public void onBuildError(String message, Exception exception) {
						listener.onBuildError(message, exception);
					}

					@Override
					public void onTaskComplete(JavaProject project) {
						// Add the built project to the system, and notify that it has completed
						system.addProject(project);
						listener.onProjectBuilt(project);
						
						// Check if all projects have been built
						if (system.allProjectsBuilt()) {
							system.stopStopwatch();
							listener.onAllProjectsBuilt(system);
						}
					}
					
				});
			});
			
			if (queue.remainingCapacity() == 0) {
				try {
					waitForTasksToComplete(queue);
				} catch (TaskBuildException e) {
					listener.onBuildError(String.format("Stopping build... %s/%s tasks were run.", i, projectCount), e);
					return;
				}
			}
		}
	}
	
	private void waitForTasksToComplete(BlockingQueue<Runnable> queue) throws TaskBuildException {
		try {
			int timeoutCount = 0;
			while (queue.remainingCapacity() == 0) {
				Thread.sleep(TIMEOUT);
				timeoutCount++;
				
				if (queue.remainingCapacity() == 0 && timeoutCount == MAX_TIMEOUTS) {
					// Wait period has passed and still no room in queue, so we need to stop...
					throw new TaskBuildException();
				}
			}
		} catch (InterruptedException e) {
			throw new TaskBuildException("Threads interrupted while waiting for tasks to complete!");
		}
	}
	
	private void runSystemBuildTask(JavaSystem system, File projectFile, BuildTaskListener listener) {
		JavaProject project = new JavaProject(projectFile, projectFile.getName(), system.getName());
		if (listener != null)
			listener.onProgressChange(String.format("Starting build task for project %s in system %s...", project.toString(), system.getName()), 0);

		build(project, false, new ProjectBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
				listener.onProgressChange(project.toString() + ": " + message, progress);
			}

			@Override
			public void finished(JavaProject project) {
				listener.onTaskComplete(project);
			}

			@Override
			public void onBuildError(String message, Exception exception) {
				listener.onBuildError(project.toString() + ": " + message, exception);
			}
		});
	}
	
	public void buildProject(File projectFile, String projectName, ProjectBuildListener listener) {
		Runnable task = () -> {
			if (listener != null)
				listener.onProgressChange(String.format("Starting to load %s...", projectName), 0);
			JavaProject project = new JavaProject(projectFile, projectName);
			build(project, false, listener);
		};

		Thread thread = new Thread(task);
		thread.start();
	}

	public void buildProject(String projectPath, String projectName, ProjectBuildListener listener) {
		Runnable task = () -> {
			listener.onProgressChange(String.format("Starting to load %s...", projectName), 0);
			JavaProject project = new JavaProject(projectPath, projectName);
			build(project, false, listener);
		};

		Thread thread = new Thread(task);
		thread.start();
	}

	private void build(JavaProject project, boolean deleteAfterBuild, ProjectBuildListener listener) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		try {
			// Get all the files for the project
			List<SourcedFile> files = mSourcer.buildSourceForProject(project, listener);

			// Create the Abstract Syntax Trees. The build listener for this is not perfect, so it will show 0% until
			// all ASTs are created, when it will then show 100% before moving to the next task.
			// TODO Maybe use IProgressMonitor with SubMonitor for this?
			// https://eclipse.org/articles/Article-Progress-Monitors/article.html
			if (listener != null)
				listener.onProgressChange("Creating ASTs...", 0);
			createASTs(project.getProjectFile().getAbsolutePath(), files);
			if (listener != null)
				listener.onProgressChange("Finished building ASTs!", 100);
			
			// Use the visitors to build the JavaProject!
			visitCompilationUnits(project, files, listener);

			String timeToComplete = stopwatch.stopAndGetTime();
			if (listener != null)
				listener.onProgressChange(String.format("Finished! Time to complete: %s", timeToComplete), 100);

			if (project != null && listener != null)
				listener.finished(project);
			
		} catch (IOException e) {
			if (listener != null)
				listener.onBuildError(String.format("Something went wrong loading the file %s.", project.getProjectFile()), e);
			logger.error(String.format("Something went wrong loading the file %s.", project.getProjectFile()), e);
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
						missingImports.add(problem.getArguments()[0]); // This is assuming a lot I think, but should be
																		// right...
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
