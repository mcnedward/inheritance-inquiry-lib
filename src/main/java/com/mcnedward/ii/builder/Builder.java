package com.mcnedward.ii.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.Job;
import com.mcnedward.ii.tasks.MonitoringExecutorService;
import com.mcnedward.ii.tasks.IIJob;
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
	private static final int TIMEOUT = 10;
	private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
	private static final int INVOKE_TIMEOUT = 5;
	private static final TimeUnit INVOKE_TIMEUNIT = TimeUnit.MINUTES;
	public static Integer COMPLETE_JOBS = 0; // Keep track of how many jobs are finished
	private BlockingQueue<Runnable> mQueue;
	private MonitoringExecutorService mExecutorService;
	private static Map<Integer, String> mTaskMap; // For keeping track of all tasks

	public Builder() {
		// Setup Threads
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Project-%d").setDaemon(true).build();
		mQueue = new ArrayBlockingQueue<>(100);
		mExecutorService = new MonitoringExecutorService(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, mQueue, threadFactory);
		mTaskMap = new HashMap<>();
	}

	public void build() throws TaskBuildException {
		COMPLETE_JOBS = 0; // Reset job count
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();

		buildProcess();

		stopwatch.stop();
		IILogger.info("Finished build! Time to complete: %s", stopwatch.toString());
	}

	public static <T> void markTaskDone(Job<T> job) {
		String jobName = mTaskMap.get(job.id());
		if (jobName != null && jobName.equals(job.name())) {
			synchronized (COMPLETE_JOBS) {
				COMPLETE_JOBS++;
			}
		}
	}

	protected abstract void buildProcess() throws TaskBuildException;

	protected <T> Future<T> submit(Job<T> task) {
		mTaskMap.put(task.id(), task.name());
		return mExecutorService.submit(task);
	}

	protected <T> List<T> invokeAll(Collection<? extends IIJob<T>> tasks) throws TaskBuildException {
		List<T> solutions = new ArrayList<>();
		tasks.forEach(task -> {
			mTaskMap.put(task.id(), task.name());
		});
		try {
			mExecutorService.invokeAll(tasks, INVOKE_TIMEOUT, INVOKE_TIMEUNIT).stream().map(future -> {
				try {
					return future.get();
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}).forEach(solutions::add);
		} catch (Exception e) {
			throw new TaskBuildException(e);
		}
		return solutions;
	}

	protected void waitForTasks() throws TaskBuildException {
		mExecutorService.shutdown();
		boolean done;
		try {
			done = mExecutorService.awaitTermination(timeout(), timeUnit());
		} catch (InterruptedException e) {
			throw new TaskBuildException("Something went wrong when trying to shutdown tasks...", e);
		} catch (Exception e) {
			throw new TaskBuildException("Something went wrong with with the tasks...", e);
		}
		IILogger.info("Shutdown tasks. Were all complete? %s [%s/%s]", done, COMPLETE_JOBS, mTaskMap.size());
		COMPLETE_JOBS = 0; // Reset job count
		mTaskMap.clear();
	}

	protected void forceShutdownExecutor() {
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

	protected int timeout() {
		return TIMEOUT;
	}
	
	protected TimeUnit timeUnit() {
		return TIMEUNIT;
	}
	
}
