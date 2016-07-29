package com.mcnedward.ii.builder;

import java.io.File;

import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.StandardBuildTask;

/**
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class ProjectBuilder extends Builder {

	private static final String PROJECT_NAME = "argouml";
	private static final String PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "argouml/argouml-0.34";//azureus/azureus-2.0.8.2";

	public ProjectBuilder() {
		super();
	}
	
	@Override
	protected void buildProcess() throws TaskBuildException {
		File buildFile = new File(PROJECT_PATH);
		if (!buildFile.exists()) {
			throw new TaskBuildException(String.format("You need to provide an existing file! [Path: %s]", buildFile.getAbsolutePath()));
		}
		
		submit(new StandardBuildTask(buildFile, PROJECT_NAME));

		waitForTasks();
	}

}
