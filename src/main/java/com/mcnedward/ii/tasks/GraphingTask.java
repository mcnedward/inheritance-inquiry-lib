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

	private static final int DIT_LIMIT = 7;
	
	public GraphingTask(File projectFile, String systemName) {
		super(projectFile, systemName);
	}

	@Override
	protected Void doWork(JavaSolution solution) throws TaskBuildException {
		try {
			graphService().buildNocHierarchyTreeGraphs(solution);
		} catch (GraphBuildException e) {
			throw new TaskBuildException(e);
		}
		return null;		
	}
	
	@Override
	protected JavaSolution analyze(JavaProject project) {
		return analyzerService().analyzeForDit(project, DIT_LIMIT);
	}

}
