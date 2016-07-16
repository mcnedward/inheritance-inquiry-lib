package com.mcnedward.ii;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.tasks.MonitoringExecutorService;
import com.mcnedward.ii.tasks.ProjectBuildTask;
import com.mcnedward.ii.utils.Constants;
import com.mcnedward.ii.utils.IILogger;

/**
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class ProjectBuilder {

	// Services
	private ProjectService mProjectService;
	private AnalyzerService mAnalyzerService;
	private MetricService mMetricService;
	// Tasks
	private static final int CORE_POOL_SIZE = 4;
	private static final int MAX_POOL_SIZE = 8;
	private static final int TIMEOUT = 20;
	public static Integer COMPLETE_JOBS = 0; // Keep track of how many jobs are finished
	private BlockingQueue<Runnable> mQueue;
	private MonitoringExecutorService mExecutorService;

	public ProjectBuilder() {
		// Setup services
		mProjectService = new ProjectService();
		mAnalyzerService = new AnalyzerService();
		mMetricService = new MetricService(Constants.METRIC_DIRECTORY_PATH);
		// Setup Threads
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Project-%d").setDaemon(true).build();
		mQueue = new ArrayBlockingQueue<>(100);
		mExecutorService = new MonitoringExecutorService(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, mQueue, threadFactory);
	}

	public JavaSystem build() throws TaskBuildException {
		File systemFile = new File(Constants.SYSTEM_PATH);
		if (systemFile.isFile()) {
			throw new TaskBuildException(String.format("You need to provide a directory for a system! [Path: %s]", Constants.SYSTEM_PATH));
		}

		JavaSystem system = new JavaSystem(systemFile);

		File[] projects = system.getFiles();
		int projectCount = projects.length;
		IILogger.info("Starting build for system %s.", system.getName());

		for (int i = 0; i < projectCount; i++) {
			File projectFile = projects[i];
			JavaProject project = mProjectService.build(projectFile, system.getName());

			IILogger.info("Finished %s/%s", i + 1, projectCount);
			int runningThreads = 0;
			for (Thread t : Thread.getAllStackTraces().keySet()) {
				if (t.getState() == Thread.State.RUNNABLE)
					runningThreads++;
			}
			IILogger.info("Active Threads: %s", runningThreads);

			system.addProject(project);
		}

		system.stopStopwatch();
		return system;
	}

	/**
	 * Builds a {@link JavaSystem} using separate jobs.
	 * 
	 * @return The finished JavaSystem
	 * @throws TaskBuildException
	 */
	public JavaSystem buildAsync() throws TaskBuildException {
		File systemFile = new File(Constants.SYSTEM_PATH);
		if (systemFile.isFile()) {
			throw new TaskBuildException(String.format("You need to provide a directory for a system! [Path: %s]", Constants.SYSTEM_PATH));
		}
		COMPLETE_JOBS = 0; // Reset job count

		JavaSystem system = new JavaSystem(systemFile);
		File[] projects = system.getFiles();
		int projectCount = projects.length;
		IILogger.info("Starting build for system %s.", system.getName());

		for (int i = 0; i < projectCount; i++) {
			File projectFile = projects[i];
			ProjectBuildTask task = new ProjectBuildTask(mProjectService, mAnalyzerService, mMetricService, projectFile, system.getName(), projectCount);
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
		mProjectService.buildProjectAsync(Constants.PROJECT_PATH, Constants.PROJECT_NAME, new ProjectBuildListener() {

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

				mMetricService.buildMetrics(mAnalyzerService.analyze(project));
			}

			@Override
			public void onBuildError(String message, Exception exception) {
				IILogger.error(message, exception);
			}

		});
	}
}
