package com.mcnedward.ii.utils;

import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.*;
import com.mcnedward.ii.service.metric.IMetricService;
import com.mcnedward.ii.service.metric.MetricService;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public class ServiceFactory {

	private static AnalyzerService mAnalyzerService;
	private static IMetricService mMetricService;
	private static IGraphService mDitGraphService;
    private static IGraphService mNocGraphService;
    private static IGraphService mFullGraphService;

	public static ProjectService projectService() {
		return new ProjectService();	// Always create this new
	}
	
	public static AnalyzerService analyzerService() {
		if (mAnalyzerService == null)
			mAnalyzerService = new AnalyzerService();
		return mAnalyzerService;
	}

	public static IMetricService metricService() {
		if (mMetricService == null)
			mMetricService = new MetricService();
		return mMetricService;
	}

	public static IGraphService ditGraphService() {
		if (mDitGraphService == null)
            mDitGraphService = new DitGraphService();
		return mDitGraphService;
	}

    public static IGraphService nocGraphService() {
        if (mNocGraphService == null)
            mNocGraphService = new NocGraphService();
        return mNocGraphService;
    }

    public static <T> IGraphService fullGraphService() {
        if (mFullGraphService == null)
            mFullGraphService = new FullGraphService();
        return mFullGraphService;
    }

}
