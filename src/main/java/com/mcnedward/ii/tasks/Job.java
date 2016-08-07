package com.mcnedward.ii.tasks;

import java.util.concurrent.Callable;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public interface Job<T> extends Callable<T> {

	String name();
	
	int id();
	
}
