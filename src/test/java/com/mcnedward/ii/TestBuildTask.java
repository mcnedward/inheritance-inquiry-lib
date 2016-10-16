package com.mcnedward.ii;

import java.io.File;

import com.mcnedward.ii.tasks.IIJob;

/**
 * @author Edward - Jul 24, 2016
 *
 */
public class TestBuildTask extends IIJob {

	public TestBuildTask() {
		super(new File("Test"));
	}

}
