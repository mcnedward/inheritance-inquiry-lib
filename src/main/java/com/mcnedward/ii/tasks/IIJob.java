package com.mcnedward.ii.tasks;

import java.io.File;
import java.util.concurrent.Callable;

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
public abstract class IIJob extends IIBaseWork implements Callable<JavaSolution> {

	public IIJob(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs) {
		super(projectService, analyzerService, metricService, graphService, projectFile, systemName, totalJobs);
	}

	public IIJob(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs, ProjectBuildListener listener) {
		super(projectService, analyzerService, metricService, graphService, projectFile, systemName, totalJobs, listener);
	}
	
	protected abstract JavaSolution doWork(JavaProject project);

	@Override
	public JavaSolution call() {
		setup();

		try {
			JavaProject project = buildProject();
			
			JavaSolution solution = doWork(project);
			
			finish(project);
			
			return solution;
		} catch (Exception e) {
			error(e);
			return null;
		}
	}

}
