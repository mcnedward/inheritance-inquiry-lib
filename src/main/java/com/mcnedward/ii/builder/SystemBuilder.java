package com.mcnedward.ii.builder;

import java.io.File;

import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.ProjectBuildTask;
import com.mcnedward.ii.tasks.TaskFactory;
import com.mcnedward.ii.utils.IILogger;

/**
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class SystemBuilder extends Builder<JavaSystem> {

	private static final String SYSTEM = "freecol";
	private static final String SYSTEM_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + SYSTEM;

	public SystemBuilder() {
		super();
	}
	
	@Override
	protected File setupFile() {
		return new File(SYSTEM_PATH);
	}
	
	/**
	 * Builds a {@link JavaSystem} using separate jobs.
	 * 
	 * @return The finished JavaSystem
	 * @throws TaskBuildException
	 */
	@Override
	protected JavaSystem doBuildProcess(File buildFile) {
		JavaSystem system = new JavaSystem(buildFile);
		File[] projects = system.getFiles();
		int projectCount = projects.length;
		IILogger.info("Starting build for system %s.", system.getName());

		for (int i = 0; i < projectCount; i++) {
			File projectFile = projects[i];
			ProjectBuildTask task = TaskFactory.createProjectBuildTask(projectFile, system.getName(), projectCount);
			submit(task);
		}
		
		return system;
	}

	@Override
	protected int getJobCount() {
		return 0;
	}

	

}
