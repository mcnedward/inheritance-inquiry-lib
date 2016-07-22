package com.mcnedward.ii.tasks;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public class DitAnalysisTask extends SolutionTask {

	public DitAnalysisTask(MetricService metricService, GraphService graphService, JavaSolution solution) {
		super(metricService, graphService, solution);
	}

	@Override
	protected void doWork(JavaSolution solution) {
	}

}
