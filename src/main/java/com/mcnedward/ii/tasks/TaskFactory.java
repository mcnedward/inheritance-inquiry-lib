package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public class TaskFactory {
	// Services
	private static AnalyzerService mAnalyzerService;
	private static MetricService mMetricService;
	private static GraphService mGraphService;
	
	private static TaskFactory mFactory;

	public TaskFactory() {
		// Setup services
		mAnalyzerService = new AnalyzerService();
		mMetricService = new MetricService();
		mGraphService = new GraphService();
	}

	public static StandardBuildTask createStandardBuildTask(File projectFile, String systemName) {
		checkContext();
		return new StandardBuildTask(mAnalyzerService, mMetricService, mGraphService, projectFile, systemName);
	}
	
	public static MetricBuildTask createMetricBuildTask(File projectFile, String systemName) {
		checkContext();
		return new MetricBuildTask(mAnalyzerService, projectFile, systemName);
	}
	
	public static DitAnalysisTask createDitAnalysisTask(JavaSolution solution) {
		checkContext();
		return new DitAnalysisTask(mMetricService, mGraphService, solution);
	}
	
	private static void checkContext() {
		if (mFactory == null)
			mFactory = new TaskFactory();
	}

}
