package com.mcnedward.ii.exception;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public class TaskBuildException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TaskBuildException(String message) {
		super(message);
	}
	
	public TaskBuildException(Exception e) {
		super(e);
	}

	public TaskBuildException(String message, Exception e) {
		super(message, e);
	}
	
}
