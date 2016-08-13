package com.mcnedward.ii.service.metric;

import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.metric.element.Metric;

/**
 * @author Edward - Aug 13, 2016
 *
 */
public abstract class MetricSection {
	
	protected String mSystemName;
	
	public MetricSection(JavaSolution solution, MType metricType) throws TaskBuildException {
		mSystemName = solution.getSystemName();
		List<? extends Metric> metrics;
		init();
		
		switch (metricType) {
		case DIT:
			metrics = solution.getDitMetrics();
			break;
		case NOC:
			metrics = solution.getNocMetrics();
			break;
		case WMC:
			metrics = solution.getWmcMetrics();
			break;
		default:
			throw new TaskBuildException("Metric type " + metricType.name() + " is not acceptable for inquiry...");
		}
		handleMetrics(metrics);
	}
	
	protected abstract void init();
	
	protected abstract void handleMetrics(List<? extends Metric> metrics);


}
