package com.mcnedward.ii;

import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.builder.Builder;
import com.mcnedward.ii.exception.TaskBuildException;
/**
 * @author Edward - Jul 24, 2016
 *
 */
public class TestBuilder extends Builder {

	List<Integer> solutions = new ArrayList<>();
	
	@Override
	protected void buildProcess() throws TaskBuildException {
		List<TestBuildTask> tasks = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			tasks.add(new TestBuildTask());
		}
		solutions = invokeAll(tasks);
	}

}
