package com.mcnedward.ii.builder;

import java.io.File;

import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.tasks.ProjectBuildTask;
import com.mcnedward.ii.tasks.TaskFactory;

/**
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class ProjectBuilder extends Builder<Void> {

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
	protected Void doBuildProcess(File buildFile) {
		ProjectBuildTask task = TaskFactory.createProjectBuildTask(buildFile, PROJECT_NAME, 1);
		submit(task);
		return null;
	}


	@Override
	protected int getJobCount() {
		return 1;
	}

	

}
