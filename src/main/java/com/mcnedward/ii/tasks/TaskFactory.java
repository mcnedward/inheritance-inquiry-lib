package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public class TaskFactory {
	// Services
	private static ProjectService mProjectService;
	private static AnalyzerService mAnalyzerService;
	private static MetricService mMetricService;
	private static GraphService mGraphService;

	public TaskFactory() {
		// Setup services
		mProjectService = new ProjectService();
		mAnalyzerService = new AnalyzerService();
		mMetricService = new MetricService();
		mGraphService = new GraphService();
	}

	public static ProjectBuildTask createProjectBuildTask(File projectFile, String systemName, int totalJobs) {
		return new ProjectBuildTask(mProjectService, mAnalyzerService, mMetricService, mGraphService, projectFile, systemName, totalJobs);
	}
	
	public static DitAnalysisTask createDitAnalysisTaskTask(File projectFile, String systemName, int totalJobs) {
		return new DitAnalysisTask(mProjectService, mAnalyzerService, mMetricService, mGraphService, projectFile, systemName, totalJobs);
	}

}
