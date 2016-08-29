package com.mcnedward.ii.exception;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public class ProjectBuildException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ProjectBuildException(String message) {
		super(message);
	}
	
	public ProjectBuildException(Exception e) {
		super(e);
	}

	public ProjectBuildException(String message, Exception e) {
		super(message, e);
	}
	
}
