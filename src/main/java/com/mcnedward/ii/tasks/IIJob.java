package com.mcnedward.ii.tasks;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.ProjectBuildException;
import com.mcnedward.ii.listener.SolutionBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.ServiceFactory;

import java.io.File;

/**
 * @author Edward - Jul 22, 2016
 */
public class IIJob implements Job<JavaSolution> {

    private static int JOB_ID = 1;
    private int mId;

    private ProjectService mProjectService;
    private AnalyzerService mAnalyzerService;
    private File mProjectFile;
    private String mSystemName;
    private SolutionBuildListener mListener;

    public IIJob(File projectFile) {
        this(projectFile, null);
    }

    public IIJob(File projectFile, SolutionBuildListener listener) {
        mId = JOB_ID++;
        mProjectService = ServiceFactory.projectService();
        mAnalyzerService = ServiceFactory.analyzerService();
        mProjectFile = projectFile;
        mSystemName = projectFile.getName();
        mListener = listener;
    }

    @Override
    public JavaSolution call() {
        IILogger.info("Starting job for %s...", mSystemName);
        try {
            JavaProject project = mProjectService.build(mProjectFile, mListener);
            JavaSolution solution = mAnalyzerService.analyze(project, mListener);
            IILogger.info("Completed job for %s...", mSystemName);
            return solution;
        } catch (ProjectBuildException e) {
            mListener.onBuildError(e.getMessage(), e);
            IILogger.error(String.format("Error in job for %s...", mSystemName), e);
        } catch (Exception e) {
            mListener.onBuildError(String.format("Something went wrong when building the project %s.", mProjectFile
                    .getName()), e);
            IILogger.error(String.format("Error in job for %s...", mSystemName), e);
        }
        return null;
    }

    @Override
    public String name() {
        return toString() + "-" + id();
    }

    @Override
    public int id() {
        return mId;
    }

    @Override
    public String toString() {
        return "SolutionTask-" + mSystemName;
    }

}
