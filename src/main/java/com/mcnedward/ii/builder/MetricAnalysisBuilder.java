package com.mcnedward.ii.builder;

import java.io.File;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.tasks.IIJob;
import com.mcnedward.ii.tasks.MetricBuildTask;
import com.mcnedward.ii.utils.ServiceFactory;

/**
 * 
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class MetricAnalysisBuilder extends QCBuilder<JavaSolution> {

	private static final String[] EXCLUSIONS = new String[] {};// { "freecol", "freemind", "hibernate" };

	public MetricAnalysisBuilder() {
		super();
	}

	@Override
	protected IIJob<JavaSolution> getJob(File systemFile, String name) {
		return new MetricBuildTask(systemFile, systemFile.getName());
	}

	@Override
	protected void handleSolutions(List<JavaSolution> solutions) throws TaskBuildException {
		MetricService service = ServiceFactory.metricService();
		service.buildSolutionsMetricLevels(solutions);
	}

	@Override
	protected String[] getExclusions() {
		return EXCLUSIONS;
	}

}
