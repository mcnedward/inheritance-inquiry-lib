package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public class MetricBuildTask extends IIJob<JavaSolution> {

	public MetricBuildTask(File projectFile, String systemName) {
		this(projectFile, systemName, null);
	}

	public MetricBuildTask(File projectFile, String systemName, ProjectBuildListener listener) {
		super(projectFile, systemName, listener);
	}

	@Override
	protected JavaSolution processSolution(JavaSolution solution) throws TaskBuildException {
		return solution;
	}
	
	@Override
	protected JavaSolution analyze(JavaProject project) {
		return analyzerService().analyzeMetrics(project);
	}

}
