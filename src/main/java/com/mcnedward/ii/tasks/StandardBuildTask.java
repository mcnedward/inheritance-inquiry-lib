package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public class StandardBuildTask implements Runnable {

	private ProjectService mProjectService;
	private AnalyzerService mAnalyzerService;
	private MetricService mMetricService;
	private GraphService mGraphService;
	private File mProjectFile;
	private String mSystemName;
	private ProjectBuildListener mListener;

	public StandardBuildTask(AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName) {
		this(analyzerService, metricService, graphService, projectFile, systemName, null);
	}

	public StandardBuildTask(AnalyzerService analyzerService, MetricService metricService, GraphService graphService, File projectFile, String systemName,
			ProjectBuildListener listener) {
		mProjectService = new ProjectService();
		mAnalyzerService = analyzerService;
		mMetricService = metricService;
		mGraphService = graphService;
		mProjectFile = projectFile;
		mSystemName = systemName;
		mListener = listener;
	}

	@Override
	public void run() {
		IILogger.info("Starting BuildTask for file %s...", mProjectFile.getName());

		try {
			JavaProject project = mProjectService.build(mProjectFile, mSystemName, mListener);
			JavaSolution solution = mAnalyzerService.analyze(project);
			mMetricService.buildMetrics(solution);
			mGraphService.buildGraphs(solution);
		} catch (Exception e) {
			IILogger.error(e);
		}
	}

	@Override
	public String toString() {
		return "JavaSolution-" + mProjectFile.getName();
	}

}
