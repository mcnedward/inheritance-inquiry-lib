package com.mcnedward.ii.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.IIJob;

/**
 * @author Edward - Jul 31, 2016
 *
 */
public abstract class QCBuilder<T> extends Builder {

//	@Override
//	protected void buildProcess() throws TaskBuildException {
//		File buildFile = new File(getQualitusCorpusPath());
//		if (!buildFile.exists()) {
//			throw new TaskBuildException(String.format("You need to provide an existing file! [Path: %s]", buildFile.getAbsolutePath()));
//		}
//
//		List<IIJob<T>> tasks = new ArrayList<>();
//
//		File[] systemDirs = buildFile.listFiles();
//		for (File systemDir : systemDirs) {
//			boolean skip = false;
//			for (String exclusion : getExclusions()) {
//				if (systemDir.getName().contains(exclusion)) {
//					skip = true;
//					break;
//				}
//			}
//			if (skip) continue;
//			File[] systemFiles = systemDir.listFiles();
//			File systemFile = systemFiles[systemFiles.length - 1];
//			tasks.add(getJob(systemFile, systemFile.getName()));
//		}
//
//		List<T> solutions = invokeAll(tasks);
//
//		// Shutdown executor service and notify of jobs finished
//		waitForTasks();
//		// Keeping this around in case we need to use it, like if we want to run tasks in the handleSolutions()
////		int totalJobs = tasks.size();
////		int completeJobs = solutions.size();
////		boolean allComplete = totalJobs == completeJobs;
////		IILogger.info("Finished build jobs. Were all complete? %s [%s/%s]", allComplete, completeJobs, totalJobs);
////		COMPLETE_JOBS = 0; // Reset job count
//
//		// handle tasks
//		handleSolutions(solutions);
//	}
	
	protected abstract IIJob getJob(File systemFile, String name);

	protected abstract void handleSolutions(List<T> solutions) throws TaskBuildException;
	
	protected abstract String[] getExclusions();

}
