package com.mcnedward.ii.tasks;

import java.io.File;
import java.util.concurrent.Callable;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public class BuildTask implements Callable<JavaSolution> {

	private ProjectService mProjectService;
	private AnalyzerService mAnalyzerService;
	private File mProjectFile;
	private String mSystemName;
	private ProjectBuildListener mListener;

	public BuildTask(ProjectService projectService, AnalyzerService analyzerService, File projectFile, String systemName) {
		this(projectService, analyzerService, projectFile, systemName, null);
	}

	public BuildTask(ProjectService projectService, AnalyzerService analyzerService, File projectFile, String systemName, ProjectBuildListener listener) {
		mProjectService = projectService;
		mAnalyzerService = analyzerService;
		mProjectFile = projectFile;
		mSystemName = systemName;
		mListener = listener;
	}

	@Override
	public JavaSolution call() {
		IILogger.info("Starting BuildTask for file %s...", mProjectFile.getName());

		try {
			JavaProject project = mProjectService.build(mProjectFile, mSystemName, mListener);
			JavaSolution solution = mAnalyzerService.analyzeDit(project);
			return solution;
		} catch (Exception e) {
			IILogger.error(e);
			return null;
		}
	}

	@Override
	public String toString() {
		return "JavaSolution-" + mProjectFile.getName();
	}

}
