package com.mcnedward.ii.exception;

/**
 * @author Edward - Jul 15, 2016
 *
 */
public class MetricExportException extends Exception {
	private static final long serialVersionUID = 1L;

	public MetricExportException(String message) {
		super(message);
	}

	public MetricExportException(Exception e) {
		super(e);
	}

	public MetricExportException(String message, Exception e) {
		super(message, e);
	}
	
}
