package com.mcnedward.ii;

import java.util.concurrent.Callable;

/**
 * @author Edward - Jul 24, 2016
 *
 */
public class TestBuildTask implements Callable<Integer> {

	private static int ID = 1;
	
	@Override
	public Integer call() throws Exception {
		return ID++;
	}

}
