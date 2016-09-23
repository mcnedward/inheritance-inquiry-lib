package com.mcnedward.ii.builder;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.ProjectBuildException;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.SolutionBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.tasks.MonitoringExecutorService;
import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.ServiceFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edward on 9/21/2016.
 */
public class ProjectBuilder {

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
    private SolutionBuildListener mListener;
    // Services
    private ProjectService mProjectService;
    private AnalyzerService mAnalyzerService;

    public ProjectBuilder(SolutionBuildListener listener) {
        this();
        mListener = listener;
    }

    public ProjectBuilder() {
        // Setup services
        mProjectService = ServiceFactory.projectService();
        mAnalyzerService = ServiceFactory.analyzerService();
    }

    public void build(String projectPath) {
        build(new File(projectPath));
    }

    public void build(File projectFile) {
        COMPLETE_JOBS = 0; // Reset job count
        setupExecutorService();
        if (!projectFile.exists()) {
            String message = String.format("You need to provide an existing file! [Path: %s]", projectFile.getAbsolutePath());
            TaskBuildException exception = new TaskBuildException(message);
            mListener.onBuildError(message, exception);
            return;
        }

        Runnable task = () -> {
            try {
                JavaProject project = mProjectService.build(projectFile, mListener);
                JavaSolution solution = mAnalyzerService.analyze(project, mListener);
                mListener.finished(solution);
            } catch (ProjectBuildException e) {
                mListener.onBuildError(e.getMessage(), e);
            } catch (Exception e) {
                mListener.onBuildError(String.format("Something went wrong when building the project %s.", projectFile
                        .getName()), e);
            }
        };
        mExecutorService.submit(task);
    }

    private void forceShutdownExecutor() {
        // Shutdown the ExecutorService now that all projects are built
        try {
            IILogger.info("Attempting to shutdown executor...");
            mExecutorService.shutdown();
            mExecutorService.awaitTermination(500, TimeUnit.MILLISECONDS);
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

    /**
     * Creates the ExecutorService to use in the build task. This is called at the beginning of the build process, but
     * this should probably be adjusted to only be created once in the constructor, and then cancel the tasks instead of
     * calling shutdown() on the ExecutorService.
     */
    private void setupExecutorService() {
        mQueue = new ArrayBlockingQueue<>(100);
        mExecutorService = new MonitoringExecutorService(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, mQueue);
        mTaskMap = new HashMap<>();
    }
}
