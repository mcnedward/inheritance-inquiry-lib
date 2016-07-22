package com.mcnedward.ii.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.BuildTask;
import com.mcnedward.ii.tasks.TaskFactory;
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
	protected Collection<BuildTask> buildSolutions(File buildFile) {
		JavaSystem system = new JavaSystem(buildFile);
		File[] projects = system.getFiles();
		IILogger.info("Starting build for system %s.", system.getName());

		List<BuildTask> tasks = new ArrayList<>();
		for (File projectFile : projects) {
			tasks.add(TaskFactory.createBuildTask(projectFile, system.getName()));
		}
		
		return tasks;
	}

	@Override
	protected int handleSolutions(List<JavaSolution> solutions) {
		return 0;
	}

	

}
