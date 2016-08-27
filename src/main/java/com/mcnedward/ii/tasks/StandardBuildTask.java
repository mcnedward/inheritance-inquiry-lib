package com.mcnedward.ii.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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
	public Void processSolution(JavaSolution solution) throws GraphBuildException {
		Collection<String> elements = new ArrayList<>();
		elements.add("FreeColObject");
		elements.add("FreeGameObject");
		elements.add("FreeColPanel");
		elements.add("FreeColDialog");
		elements.add("FreeColAction");
		elements.add("Mission");
		graphService().buildNocHierarchyTreeGraphs(solution, elements);
		return null;
	}

	@Override
	protected JavaSolution analyze(JavaProject project) {
		return analyzerService().analyzeForNoc(project);
	}

}
