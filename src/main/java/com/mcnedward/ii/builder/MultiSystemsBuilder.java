package com.mcnedward.ii.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.tasks.IIJob;
import com.mcnedward.ii.tasks.MetricBuildTask;
import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.ServiceFactory;

/**
 * 
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class MultiSystemsBuilder extends QCBuilder<JavaSolution> {

	private static final String[] EXCLUSIONS = { "antlr", "argouml", "azureus", "freecol", "jhotdraw", "hibernate", "jung", "junit", "lucene", "weka" };

	public MultiSystemsBuilder() {
		super();
	}

	@Override
	protected IIJob<JavaSolution> getJob(File systemFile, String name) {
		return new MetricBuildTask(systemFile, systemFile.getName());
	}

	@Override
	protected void handleSolutions(List<JavaSolution> solutions) throws TaskBuildException {
		MetricService service = ServiceFactory.metricService();
		for (JavaSolution solution : solutions) {
			service.buildSolutionDetails(solution);
			List<String> nocMaxClass = solution.getNocMetricInfo().getMaxClasses();
			IILogger.info("NOC max class: %s in %s", nocMaxClass, solution);

			Collection<String> elements = new ArrayList<>();
			elements.add("Taskdef");
			try {
				ServiceFactory.graphService().buildDitHierarchyTreeGraphs(solution, 7);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected String[] getExclusions() {
		return EXCLUSIONS;
	}

}
