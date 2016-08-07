package com.mcnedward.ii.builder;

import java.io.File;
import java.util.List;

import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.GraphingTask;
import com.mcnedward.ii.tasks.IIJob;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public final class GraphBuilder extends QQBuilder<Void> {

	@Override
	protected IIJob<Void> getJob(File systemFile, String name) {
		return new GraphingTask(systemFile, name);
	}

	@Override
	protected void handleSolutions(List<Void> solutions) throws TaskBuildException {
	}

	@Override
	protected String[] getExclusions() {
		return new String[] {};
	}
	
	@Override
	protected int timeout() {
		return 5;
	}

}
