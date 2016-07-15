package com.mcnedward.ii.tasks;

import java.util.concurrent.Callable;

import com.mcnedward.ii.builder.MetricBuilder;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 15, 2016
 *
 */
public class MetricBuildTask implements Callable<Boolean> {

	private MetricBuilder mMetricBuilder;
	private JavaProject mProject;

	public MetricBuildTask(String metricDirectoryPath, JavaProject project) {
		mMetricBuilder = new MetricBuilder(metricDirectoryPath);
		mProject = project;
	}

	@Override
	public Boolean call() throws Exception {
		return mMetricBuilder.buildMetrics(mProject);
	}

}
