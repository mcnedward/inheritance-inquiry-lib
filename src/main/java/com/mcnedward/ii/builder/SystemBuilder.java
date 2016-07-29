package com.mcnedward.ii.builder;

import java.io.File;

import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.StandardBuildTask;
import com.mcnedward.ii.utils.IILogger;

/**
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class SystemBuilder extends Builder {

	private static final String SYSTEM = "freecol";
	private static final String SYSTEM_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + SYSTEM;

	public SystemBuilder() {
		super();
	}
	
	/**
	 * Builds a {@link JavaSystem} using separate jobs.
	 * 
	 * @return The finished JavaSystem
	 * @throws TaskBuildException
	 */
	@Override
	protected void buildProcess() throws TaskBuildException {
		File buildFile = new File(SYSTEM_PATH);
		if (!buildFile.exists()) {
			throw new TaskBuildException(String.format("You need to provide an existing file! [Path: %s]", buildFile.getAbsolutePath()));
		}
		
		JavaSystem system = new JavaSystem(buildFile);
		File[] projects = system.getFiles();
		IILogger.info("Starting build for system %s.", system.getName());

		for (File projectFile : projects) {
			submit(new StandardBuildTask(projectFile, system.getName()));
		}
		
		waitForTasks();
	}

}
