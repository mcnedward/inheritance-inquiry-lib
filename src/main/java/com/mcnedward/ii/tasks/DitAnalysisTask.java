package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public class DitAnalysisTask extends IIJob {

	private AnalyzerService mAnalyzerService;

	public DitAnalysisTask(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs) {
		this(projectService, analyzerService, metricService, graphService, projectFile, systemName, totalJobs, null);
	}

	public DitAnalysisTask(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs, ProjectBuildListener listener) {
		super(projectService, analyzerService, metricService, graphService, projectFile, systemName, totalJobs, listener);
	}

	@Override
	protected JavaSolution doWork(JavaProject project) {
		return mAnalyzerService.analyzeDit(project);
	}

	@Override
	public String getName() {
		return "DitAnalysisTask-" + name();
	}

}
