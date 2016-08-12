package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.exception.TaskBuildException;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public class GraphingTask extends IIJob<Void> {

	public GraphingTask(File projectFile, String systemName) {
		super(projectFile, systemName);
	}

	@Override
	protected Void processSolution(JavaSolution solution) throws TaskBuildException {
		try {
//			graphService().buildDitHierarchyTreeGraph(solution);
			graphService().buildNocHierarchyTreeGraphs(solution, 39);
		} catch (GraphBuildException e) {
			throw new TaskBuildException(e);
		}
		return null;		
	}
	
	@Override
	protected JavaSolution analyze(JavaProject project) {
		return analyzerService().analyzeForNoc(project);
	}

}
