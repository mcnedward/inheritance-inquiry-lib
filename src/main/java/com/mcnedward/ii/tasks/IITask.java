package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public abstract class IITask extends IIBaseWork implements Runnable {

	public IITask(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs) {
		super(projectService, analyzerService, metricService, graphService, projectFile, systemName, totalJobs);
	}

	public IITask(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs, ProjectBuildListener listener) {
		super(projectService, analyzerService, metricService, graphService, projectFile, systemName, totalJobs, listener);
	}

	protected abstract void doWork(JavaProject project);

	@Override
	public void run() {
		setup();

		try {
			JavaProject project = buildProject();
			
			doWork(project);
			
			finish(project);
		} catch (Exception e) {
			error(e);
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}
