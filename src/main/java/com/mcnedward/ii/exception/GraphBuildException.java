package com.mcnedward.ii.exception;

/**
 * @author Edward - Jul 15, 2016
 *
 */
public class GraphBuildException extends Exception {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MESSAGE = "There was a problem building the graphs.";
	
	public GraphBuildException(String message) {
		super(DEFAULT_MESSAGE + ": " + message);
	}

	public GraphBuildException(Exception e) {
		super(e);
	}
	
	public GraphBuildException(String message, Exception e) {
		super(DEFAULT_MESSAGE + ": " + message, e);
	}
	
}
