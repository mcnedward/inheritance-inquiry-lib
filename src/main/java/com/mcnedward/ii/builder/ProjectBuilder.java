package com.mcnedward.ii.builder;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.ProjectBuildException;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.SolutionBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.utils.ServiceFactory;

import java.io.File;

/**
 * Created by Edward on 9/21/2016.
 */
public class ProjectBuilder extends Builder {

    private File mProjectFile;
    private String mProjectName;
    private SolutionBuildListener mListener;
    // Services
    private ProjectService mProjectService;
    private AnalyzerService mAnalyzerService;

    public ProjectBuilder(SolutionBuildListener listener) {
        super();
        mListener = listener;
        // Setup services
        mProjectService = ServiceFactory.projectService();
        mAnalyzerService = ServiceFactory.analyzerService();
    }

    /**
     * Call this to setup the ProjectBuilder BEFORE calling the build() method!
     *
     * @param projectFile The File that will be used in the build process.
     * @return This ProjectBuilder
     */
    public ProjectBuilder setup(File projectFile) {
        mProjectFile = projectFile;
        return this;
    }

    public ProjectBuilder setup(File projectFile, String projectName) {
        mProjectFile = projectFile;
        mProjectName = projectName;
        return this;
    }

    @Override
    protected Runnable buildTask() {
        if (mProjectFile == null) {
            mListener.onBuildError("Something went wrong during the build process.", new IllegalStateException("Project file not set. You need to call setup method first!"));
        }
        if (!mProjectFile.exists()) {
            String message = String.format("You need to provide an existing file! [Path: %s]", mProjectFile.getAbsolutePath());
            TaskBuildException exception = new TaskBuildException(message);
            mListener.onBuildError(message, exception);
            return null;
        }

        return () -> {
            try {
                JavaProject project = mProjectService.build(mProjectFile, mProjectName, mListener);
                JavaSolution solution = mAnalyzerService.analyze(project, mListener);
                mListener.finished(solution);
            } catch (ProjectBuildException e) {
                mListener.onBuildError(e.getMessage(), e);
            } catch (Exception e) {
                mListener.onBuildError(String.format("Something went wrong when building the project %s.", mProjectFile
                        .getName()), e);
            } finally {
                reset();
            }
        };
    }

    @Override

    protected void reset() {
        mProjectFile = null;
        mProjectName = null;
    }
}
