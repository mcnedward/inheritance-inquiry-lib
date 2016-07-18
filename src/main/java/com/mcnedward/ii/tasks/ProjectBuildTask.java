package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.ProjectBuilder;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public class ProjectBuildTask implements Runnable {

	private ProjectService mProjectService;
	private AnalyzerService mAnalyzerService;
	private MetricService mMetricService;
	private GraphService mGraphService;

	private File mProjectFile;
	private String mSystemName;
	private int mTotalJobs;
	private ProjectBuildListener mListener;

	public ProjectBuildTask(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs) {
		mProjectService = projectService;
		mAnalyzerService = analyzerService;
		mMetricService = metricService;
		mGraphService = graphService;
		mProjectFile = projectFile;
		mSystemName = systemName;
		mTotalJobs = totalJobs;
	}

	public ProjectBuildTask(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs, ProjectBuildListener listener) {
		this(projectService, analyzerService, metricService, graphService, projectFile, systemName, totalJobs);
		mListener = listener;
	}

	@Override
	public void run() {
		IILogger.info("Starting build job for file %s in system %s", mProjectFile.getName(), mSystemName);

		try {
			JavaProject project = mProjectService.build(mProjectFile, mSystemName, mListener);

			JavaSolution solution = mAnalyzerService.analyze(project);

//			boolean metricsBuilt = mMetricService.buildMetrics(solution);

			boolean graphsBuilt = mGraphService.buildGraphs(solution);

			synchronized (ProjectBuilder.COMPLETE_JOBS) {
				IILogger.info("Finished build job for %s in system %s [%s/%s]\nMetrics built? %s\nGraphs built? %s", project.toString(), mSystemName,
						++ProjectBuilder.COMPLETE_JOBS, mTotalJobs, false, graphsBuilt);
			}
		} catch (Exception e) {
			IILogger.error(String.format("There was a problem running the task for file %s...", mProjectFile.getName()), e);
		}
	}

	public String getName() {
		return "ProjectBuildTask-" + mProjectFile.getName();
	}

	@Override
	public String toString() {
		return getName();
	}

}
