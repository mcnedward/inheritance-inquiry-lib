package com.mcnedward.ii.exception;

/**
 * @author Edward - Jul 15, 2016
 *
 */
public class MetricBuildException extends Exception {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MESSAGE = "There was a problem building the metrics.";
	
	public MetricBuildException(String message) {
		super(DEFAULT_MESSAGE + ": " + message);
	}
	
	public MetricBuildException(String message, Exception e) {
		super(DEFAULT_MESSAGE + ": " + message, e);
	}
	
}
