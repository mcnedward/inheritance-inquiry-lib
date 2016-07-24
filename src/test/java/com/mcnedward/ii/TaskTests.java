package com.mcnedward.ii;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.mcnedward.ii.exception.TaskBuildException;

/**
 * @author Edward - Jul 24, 2016
 *
 */
public class TaskTests {

	@Test
	public void invokeAll_Works() throws TaskBuildException {
		TestBuilder builder = new TestBuilder();
		builder.build();
		
		List<Integer> solutions = builder.solutions;
		
		assertThat(solutions.isEmpty(), is(false));
	}
	
	
}
