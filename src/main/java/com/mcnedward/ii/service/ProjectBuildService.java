package com.mcnedward.ii.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.mcnedward.ii.builder.MetricBuilder;
import com.mcnedward.ii.builder.ProjectBuilder;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.tasks.ProjectBuildTask;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public class ProjectBuildService {

	// Directory paths
	private static final String QUALITUS_CORPUS_SYSTEMS_PATH = "C:/QC/pt1/Systems/";
	private static final String GRAPH_DIRECTORY_PATH = "C:/users/edward/dev/IIGraphs";

	// For buildSystem()
	private static final String SYSTEM = "azureus";
	private static final String SYSTEM_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + SYSTEM;

	// For buildProject()
	private static final String PROJECT_NAME = "azureus";
	private static final String PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "azureus/azureus-2.0.8.2";

	// Tasks
	private static final int MAX_TASKS = 10;
	private ExecutorService mExecutorService;

	private ProjectBuilder mInheritanceInquiry;
	private MetricBuilder mGraphBuilder;

	public ProjectBuildService() {
		mInheritanceInquiry = new ProjectBuilder();
		mGraphBuilder = new MetricBuilder(GRAPH_DIRECTORY_PATH);
		mExecutorService = Executors.newWorkStealingPool();;
	}

	public JavaSystem build() throws TaskBuildException {
		File systemFile = new File(SYSTEM_PATH);
		if (systemFile.isFile()) {
			throw new TaskBuildException(String.format("You need to provide a directory for a system! [Path: %s]", SYSTEM_PATH));
		}

		JavaSystem system = new JavaSystem(systemFile);
		File[] projects = system.getFiles();
		int projectCount = projects.length;
		IILogger.info("Starting build for system %s.", system.getName());

		List<Callable<JavaProject>> tasks = new ArrayList<>();
		ProjectBuilder builder = new ProjectBuilder();
		for (int i = 0; i < projectCount; i++) {
			File project = projects[i];
			Callable<JavaProject> task = new ProjectBuildTask(builder, system, project);
			tasks.add(task);
			
			if (tasks.size() == MAX_TASKS) {
				try {
					mExecutorService.invokeAll(tasks)
					.stream()
					.map(future -> {
						try {
							return future.get();
						} catch (Exception e) {
							throw new IllegalStateException("Something went wrong when invoking the project task for future: " + future, e);
						}
					})
					.forEach(system::addProject);
				} catch (InterruptedException e) {
					throw new TaskBuildException(e);
				}
			}
		}
		shutdownExecutor();
		
		return system;
	}
	
	private void shutdownExecutor() {
		// Shutdown the ExecutorService now that all projects are built
		try {
			IILogger.info("Attempting to shutdown executor...");
			mExecutorService.shutdown();
			mExecutorService.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			IILogger.info("Build tasks were interrupted...");
		} finally {
			if (!mExecutorService.isTerminated()) {
				IILogger.info("Canceling non-finished build tasks...");
			}
			mExecutorService.shutdownNow();
			IILogger.info("Shutdown complete.");
		}
	}

	public void buildProject() {
		mInheritanceInquiry.buildProject(PROJECT_PATH, PROJECT_NAME, new ProjectBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
				IILogger.info("%s - %s", message, progress);
			}

			@Override
			public void finished(JavaProject project) {
				System.out.println(project);
				System.out.println("Number of classes: " + project.getClasses().size());
				System.out.println("Number of interfaces: " + project.getInterfaces().size());
				System.out.println();

				mGraphBuilder.buildMetrics(project);
			}

			@Override
			public void onBuildError(String message, Exception exception) {
				IILogger.error(message, exception);
			}

		});
	}

}
