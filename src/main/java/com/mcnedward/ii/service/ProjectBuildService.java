package com.mcnedward.ii.service;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mcnedward.ii.builder.MetricBuilder;
import com.mcnedward.ii.builder.ProjectBuilder;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.tasks.MonitoringExecutorService;
import com.mcnedward.ii.tasks.ProjectBuildTask;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public final class ProjectBuildService {

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
	private static final int CORE_POOL_SIZE = 4;
	private static final int MAX_POOL_SIZE = 8;
	private static final int TIMEOUT = 20;
	public static Integer COMPLETE_JOBS = 0;	// Keep track of how many jobs are finished
	private BlockingQueue<Runnable> mQueue;
	private MonitoringExecutorService mExecutorService;

	private ProjectBuilder mInheritanceInquiry;
	private MetricBuilder mGraphBuilder;

	public ProjectBuildService() {
		mInheritanceInquiry = new ProjectBuilder();
		mGraphBuilder = new MetricBuilder(GRAPH_DIRECTORY_PATH);
		// Setup Threads
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Project-%d").setDaemon(true).build();
		mQueue = new ArrayBlockingQueue<>(100);

		mExecutorService = new MonitoringExecutorService(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, mQueue, threadFactory);
	}
	
	public JavaSystem build() throws TaskBuildException {
		File systemFile = new File(SYSTEM_PATH);
		if (systemFile.isFile()) {
			throw new TaskBuildException(String.format("You need to provide a directory for a system! [Path: %s]", SYSTEM_PATH));
		}

		JavaSystem system = new JavaSystem(systemFile);
		ProjectBuilder projectBuilder = new ProjectBuilder();

		File[] projects = system.getFiles();
		int projectCount = projects.length;
		IILogger.info("Starting build for system %s.", system.getName());

		for (int i = 0; i < projectCount; i++) {
			File projectFile = projects[i];
			JavaProject project = projectBuilder.build(projectFile, system.getName());

			IILogger.info("Finished %s/%s", i + 1, projectCount);
			int runningThreads = 0;
			for (Thread t : Thread.getAllStackTraces().keySet()) {
			    if (t.getState() == Thread.State.RUNNABLE) runningThreads++;
			}
			IILogger.info("Active Threads: %s", runningThreads);
			
			system.addProject(project);
		}
		
		system.stopStopwatch();
		return system;
	}

	/**
	 * Builds a {@link JavaSystem} using separate jobs.
	 * @return The finished JavaSystem
	 * @throws TaskBuildException
	 */
	public JavaSystem buildAsync() throws TaskBuildException {
		File systemFile = new File(SYSTEM_PATH);
		if (systemFile.isFile()) {
			throw new TaskBuildException(String.format("You need to provide a directory for a system! [Path: %s]", SYSTEM_PATH));
		}
		COMPLETE_JOBS = 0; // Reset job count

		JavaSystem system = new JavaSystem(systemFile);
		File[] projects = system.getFiles();
		int projectCount = projects.length;
		IILogger.info("Starting build for system %s.", system.getName());

		for (int i = 0; i < projectCount; i++) {
			File projectFile = projects[i];
			ProjectBuildTask task = new ProjectBuildTask(projectFile, system.getName(), projectCount);
			mExecutorService.submit(task);
		}

		waitForTasks(projectCount);
		system.stopStopwatch();
		return system;
	}

	private void waitForTasks(int jobCount) throws TaskBuildException {
		mExecutorService.shutdown();
		boolean done;
		try {
			done = mExecutorService.awaitTermination(TIMEOUT, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new TaskBuildException("Something went wrong when trying to shutdown tasks...", e);
		}
		IILogger.info("Shutdown tasks. Were all complete? %s [%s/%s]", done, COMPLETE_JOBS, jobCount);
		COMPLETE_JOBS = 0; // Reset job count
	}

	@SuppressWarnings("unused")
	private void forceShutdownExecutor() {
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
		mInheritanceInquiry.buildProjectAsync(PROJECT_PATH, PROJECT_NAME, new ProjectBuildListener() {

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
