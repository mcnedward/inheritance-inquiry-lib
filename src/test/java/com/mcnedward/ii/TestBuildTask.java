package com.mcnedward.ii;

import java.io.File;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.IIJob;

/**
 * @author Edward - Jul 24, 2016
 *
 */
public class TestBuildTask extends IIJob<Integer> {

	public TestBuildTask() {
		super(new File("Test"), "Test System");
	}

	private static int ID = 1;
	
	@Override
	protected Integer doWork(JavaSolution solution) throws TaskBuildException {
		return ID++;
	}

}
