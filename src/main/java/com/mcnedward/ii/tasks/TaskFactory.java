package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaSolution;
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
	
	private static TaskFactory mFactory;

	public TaskFactory() {
		// Setup services
		mProjectService = new ProjectService();
		mAnalyzerService = new AnalyzerService();
		mMetricService = new MetricService();
		mGraphService = new GraphService();
	}

	public static BuildTask createBuildTask(File projectFile, String systemName) {
		checkContext();
		return new BuildTask(mProjectService, mAnalyzerService, projectFile, systemName);
	}
	
	public static DitAnalysisTask createDitAnalysisTaskTask(JavaSolution solution) {
		checkContext();
		return new DitAnalysisTask(mMetricService, mGraphService, solution);
	}
	
	private static void checkContext() {
		if (mFactory == null)
			mFactory = new TaskFactory();
	}

}
