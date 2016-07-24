package com.mcnedward.ii.builder;

import java.io.File;

import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
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
	protected void buildProcess() throws TaskBuildException {
		File buildFile = new File(PROJECT_PATH);
		if (!buildFile.exists()) {
			throw new TaskBuildException(String.format("You need to provide an existing file! [Path: %s]", buildFile.getAbsolutePath()));
		}
		
		submit(TaskFactory.createStandardBuildTask(buildFile, PROJECT_NAME));

		waitForTasks(1);
	}

}
