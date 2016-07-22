package com.mcnedward.ii.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.BuildTask;
import com.mcnedward.ii.tasks.MonitoringExecutorService;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public abstract class Builder {

	// Directory paths
	public static final String QUALITUS_CORPUS_SYSTEMS_PATH = "C:/QC/pt1/Systems/";

	// Tasks
	private static final int CORE_POOL_SIZE = 4;
	private static final int MAX_POOL_SIZE = 8;
	private static final int TIMEOUT = 20;
	private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
	public static Integer COMPLETE_JOBS = 0; // Keep track of how many jobs are finished
	private BlockingQueue<Runnable> mQueue;
	private MonitoringExecutorService mExecutorService;
	
	public Builder() {
		// Setup Threads
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Project-%d").setDaemon(true).build();
		mQueue = new ArrayBlockingQueue<>(100);
		mExecutorService = new MonitoringExecutorService(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, mQueue, threadFactory);
	}
	
	public void build() throws TaskBuildException {
		COMPLETE_JOBS = 0; // Reset job count
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		
		File buildFile = setupFile();
		if (!buildFile.exists()) {
			throw new TaskBuildException(String.format("You need to provide an existing file! [Path: %s]", buildFile.getAbsolutePath()));
		}
		
		Collection<BuildTask> tasks = buildSolutions(buildFile);
		List<JavaSolution> solutions = new ArrayList<>();
		
		try {
			mExecutorService.invokeAll(tasks, TIMEOUT, TIMEUNIT)
				.stream()
				.map(future -> {
					try {
						return future.get();
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
				})
				.forEach(solutions::add);
		} catch (Exception e) {
			throw new TaskBuildException(e);
		}

		int totalJobs = tasks.size();
		int completeJobs = solutions.size();
		boolean allComplete = totalJobs == completeJobs;
		IILogger.info("Finished build jobs. Were all complete? %s [%s/%s]", allComplete, completeJobs, totalJobs);
		COMPLETE_JOBS = 0; // Reset job count
		
		int extraTasks = handleSolutions(solutions);
		
		waitForTasks(extraTasks);
		
		stopwatch.stop();
		IILogger.info("Finished build! Time to complete: %s", stopwatch.toString());
	}
	
	protected abstract File setupFile();
	
	protected abstract Collection<BuildTask> buildSolutions(File buildFile);
	
	protected abstract int handleSolutions(List<JavaSolution> solutions);
	
	private void waitForTasks(int jobCount) throws TaskBuildException {
		if (jobCount == 0) return;
		mExecutorService.shutdown();
		boolean done;
		try {
			done = mExecutorService.awaitTermination(TIMEOUT, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new TaskBuildException("Something went wrong when trying to shutdown tasks...", e);
		} catch (Exception e) {
			throw new TaskBuildException("Something went wrong with with the tasks...", e);
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
	
}
