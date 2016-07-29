package com.mcnedward.ii.tasks;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public abstract class SolutionTask implements Runnable {

	private MetricService mMetricService;
	private GraphService mGraphService;
	private JavaSolution mSolution;
	
	public SolutionTask(MetricService metricService, GraphService graphService, JavaSolution solution) {
		mMetricService = metricService;
		mGraphService = graphService;
		mSolution = solution;
	}

	protected abstract void doWork(JavaSolution solution);
	
	@Override
	public void run() {
		IILogger.info("Starting SolutionTask for %s...", mSolution.toString());
		try {
			doWork(mSolution);
		} catch (Exception e) {
			IILogger.error(String.format("Error in SolutionTask for %s...", mSolution.toString()), e);
		}
		IILogger.info("Completed SolutionTask for %s...", mSolution.toString());
	}
	
	protected MetricService metricService() {
		return mMetricService;
	}
	
	protected GraphService graphService() {
		return mGraphService;
	}

	@Override
	public String toString() {
		return "SolutionTask-" + mSolution.getProjectName();
	}

}
