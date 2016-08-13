package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.exception.TaskBuildException;
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
	public Void processSolution(JavaSolution solution) throws GraphBuildException {
//		metricService().buildMetrics(solution);
//		graphService().buildDitHierarchyTreeGraph(solution, 7);
		try {
			metricService().buildSolutionDetails(solution);
		} catch (TaskBuildException e) {
			throw new GraphBuildException(e);
		}
		return null;
	}

	@Override
	protected JavaSolution analyze(JavaProject project) {
		return analyzerService().analyze(project);
	}

}
