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

/**
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class DitAnalysisBuilder extends Builder {

	public DitAnalysisBuilder() {
		super();
	}
	
	@Override
	protected File setupFile() {
		return new File(QUALITUS_CORPUS_SYSTEMS_PATH);
	}
	
	/**
	 * Builds a {@link JavaSystem} using separate jobs.
	 * 
	 * @return The finished JavaSystem
	 * @throws TaskBuildException
	 */
	@Override
	protected Collection<BuildTask> buildSolutions(File buildFile) {
		List<BuildTask> tasks = new ArrayList<>();
		File[] systemDirs = buildFile.listFiles();
		for (File systemDir : systemDirs) {
			File[] systemFiles = systemDir.listFiles();
			File systemFile = systemFiles[systemFiles.length - 1];
			
			tasks.add(TaskFactory.createBuildTask(systemFile, systemFile.getName()));
		}
		return tasks;
	}

	@Override
	protected int handleSolutions(List<JavaSolution> buildResult) {
		return 0;
	}

}
