package com.mcnedward.ii.builder;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.BuildListener;
import com.mcnedward.ii.tasks.IIJob;
import com.mcnedward.ii.tasks.Job;
import com.mcnedward.ii.tasks.MonitoringExecutorService;
import com.mcnedward.ii.utils.IILogger;

import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Edward - Jul 22, 2016
 */
public abstract class Builder<T> {

    // Tasks
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 8;
    private static final int TIMEOUT = 10;
    private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private static final int INVOKE_TIMEOUT = 5;
    private static final TimeUnit INVOKE_TIME_UNIT = TimeUnit.MINUTES;
    public static Integer COMPLETE_JOBS = 0; // Keep track of how many jobs are finished
    private BlockingQueue<Runnable> mQueue;
    private MonitoringExecutorService mExecutorService;
    private static Map<Integer, String> mTaskMap; // For keeping track of all tasks
    // Listener
    private BuildListener mListener;

    public Builder(BuildListener listener) {
        this();
        mListener = listener;
    }

    public Builder() {
        // Setup Threads
        mQueue = new ArrayBlockingQueue<>(100);
        mExecutorService = new MonitoringExecutorService(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, mQueue);
        mTaskMap = new HashMap<>();
    }

    public void build(String projectPath) {
        build(new File(projectPath));
    }

    public void build(File project) {
        COMPLETE_JOBS = 0; // Reset job count
        if (!project.exists()) {
            String message = String.format("You need to provide an existing file! [Path: %s]", project.getAbsolutePath());
            TaskBuildException exception = new TaskBuildException(message);
            mListener.onBuildError(message, exception);
            return;
        }
        try {
            buildProcess(project);
        } catch (TaskBuildException e) {
            e.printStackTrace();
            forceShutdownExecutor();
            mListener.onBuildError(e.getMessage(), e);
        }
        IILogger.info("Finished build!");
    }

    protected abstract void buildProcess(File project) throws TaskBuildException;

    /**
     * Used to mark an IIJob as complete, in the case of running multiple jobs at a time. This is not used for now,
     * until I can find a better way to manage this other than a public static method call.
     *
     * @param job
     * @param <T>
     */
    private static <T> void markTaskDone(Job<T> job) {
        String jobName = mTaskMap.get(job.id());
        if (jobName != null && jobName.equals(job.name())) {
            synchronized (COMPLETE_JOBS) {
                COMPLETE_JOBS++;
            }
        }
    }

    protected <T> Future<T> submit(Job<T> task) {
        mTaskMap.put(task.id(), task.name());
        return mExecutorService.submit(task);
    }

    protected List<JavaSolution> invokeAll(Collection<? extends IIJob> tasks) throws TaskBuildException {
        List<JavaSolution> solutions = new ArrayList<>();
        tasks.forEach(task -> mTaskMap.put(task.id(), task.name()));
        try {
            mExecutorService.invokeAll(tasks, INVOKE_TIMEOUT, INVOKE_TIME_UNIT).stream().map(future -> {
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
            done = mExecutorService.awaitTermination(TIMEOUT, TIMEUNIT);
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
            IILogger.error("Build tasks were interrupted...", e);
        } finally {
            if (!mExecutorService.isTerminated()) {
                IILogger.info("Canceling non-finished build tasks...");
            }
            mExecutorService.shutdownNow();
            IILogger.info("Shutdown complete.");
        }
    }

    public BuildListener getListener() {
        return mListener;
    }

}
