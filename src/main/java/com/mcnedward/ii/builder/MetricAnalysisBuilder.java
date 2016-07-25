package com.mcnedward.ii.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.metric.MetricTool;
import com.mcnedward.ii.tasks.MetricBuildTask;
import com.mcnedward.ii.tasks.TaskFactory;
import com.mcnedward.ii.utils.IILogger;

/**
 * 
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class MetricAnalysisBuilder extends Builder {

	private static final String[] EXCLUSIONS = new String[] { "freecol", "freemind", "hibernate" };

	public MetricAnalysisBuilder() {
		super();
	}

	@Override
	protected void buildProcess() throws TaskBuildException {
		File buildFile = new File(QUALITUS_CORPUS_SYSTEMS_PATH);
		if (!buildFile.exists()) {
			throw new TaskBuildException(String.format("You need to provide an existing file! [Path: %s]", buildFile.getAbsolutePath()));
		}

		List<MetricBuildTask> tasks = new ArrayList<>();

		File[] systemDirs = buildFile.listFiles();
		for (File systemDir : systemDirs) {
			boolean skip = false;
			for (String exclusion : EXCLUSIONS) {
				if (systemDir.getName().contains(exclusion)) {
					skip = true;
					break;
				}
			}
			if (skip) continue;
			File[] systemFiles = systemDir.listFiles();
			File systemFile = systemFiles[systemFiles.length - 1];
			tasks.add(TaskFactory.createMetricBuildTask(systemFile, systemFile.getName()));
		}

		List<JavaSolution> solutions = invokeAll(tasks);

		int totalJobs = tasks.size();
		int completeJobs = solutions.size();
		boolean allComplete = totalJobs == completeJobs;
		IILogger.info("Finished build jobs. Were all complete? %s [%s/%s]", allComplete, completeJobs, totalJobs);
		COMPLETE_JOBS = 0; // Reset job count

		// handle tasks
		handleSolutions(solutions);
	}

	private void handleSolutions(List<JavaSolution> solutions) throws TaskBuildException {
		MetricTool tool = new MetricTool();
		tool.inquire(solutions);
	}

}
