package com.mcnedward.ii.utils;

import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public class ServiceFactory {

	private static AnalyzerService mAnalyzerService;
	private static MetricService mMetricService;
	private static GraphService mGraphService;

	public static ProjectService projectService() {
		return new ProjectService();	// Always create this new
	}
	
	public static AnalyzerService analyzerService() {
		if (mAnalyzerService == null)
			mAnalyzerService = new AnalyzerService();
		return mAnalyzerService;
	}

	public static MetricService metricService() {
		if (mMetricService == null)
			mMetricService = new MetricService();
		return mMetricService;
	}

	public static GraphService graphService() {
		if (mGraphService == null)
			mGraphService = new GraphService();
		return mGraphService;
	}

}
