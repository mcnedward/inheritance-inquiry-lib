package com.mcnedward.ii.builder;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.tasks.BuildTask;
import com.mcnedward.ii.tasks.TaskFactory;

/**
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class ProjectBuilder extends Builder {

	private static final String PROJECT_NAME = "azureus";
	private static final String PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "azureus/azureus-2.0.8.2";

	public ProjectBuilder() {
		super();
	}
	
	@Override
	protected File setupFile() {
		return new File(PROJECT_PATH);
	}


	@Override
	protected Collection<BuildTask> buildSolutions(File buildFile) {
		BuildTask task = TaskFactory.createBuildTask(buildFile, PROJECT_NAME);
		return Arrays.asList(task);
	}

	@Override
	protected int handleSolutions(List<JavaSolution> solutions) {
		return 0;
	}

}
