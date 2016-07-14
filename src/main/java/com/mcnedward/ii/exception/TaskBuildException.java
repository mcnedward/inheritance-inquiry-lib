package com.mcnedward.ii.exception;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public class TaskBuildException extends Exception {
	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "Task builds took too long! We have to stop...";
	
	
	public TaskBuildException() {
		super(MESSAGE);
	}
	
	public TaskBuildException(String message) {
		super(message + "\n" + MESSAGE);
	}

}
