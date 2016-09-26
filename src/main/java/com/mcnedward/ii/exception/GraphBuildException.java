package com.mcnedward.ii.exception;

/**
 * @author Edward - Jul 15, 2016
 *
 */
public class GraphBuildException extends Exception {
	private static final long serialVersionUID = 1L;

	public GraphBuildException(String message) {
		super(message);
	}

	public GraphBuildException(Exception e) {
		super(e);
	}
	
	public GraphBuildException(String message, Exception e) {
		super(message, e);
	}
	
}
