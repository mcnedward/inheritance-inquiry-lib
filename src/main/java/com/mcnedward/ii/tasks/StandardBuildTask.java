package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public class StandardBuildTask extends IIJob<Void> {

	public StandardBuildTask(File projectFile, String systemName) {
		this(projectFile, systemName, null);
	}

	public StandardBuildTask(File projectFile, String systemName, ProjectBuildListener listener) {
		super(projectFile, systemName, listener);
	}

	@Override
	public Void doWork(JavaSolution solution) throws GraphBuildException {
//		metricService().buildMetrics(solution);
//		graphService().buildGraphs(solution);
		graphService().buildFullHierarchyTreeGraph(solution, "Figure");
		return null;
	}

	@Override
	protected JavaSolution analyze(JavaProject project) {
		return analyzerService().analyzeForFullHierarchy(project, "Figure");
	}

}
