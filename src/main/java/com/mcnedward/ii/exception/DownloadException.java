package com.mcnedward.ii.exception;

/**
 * @author Edward - Jun 25, 2016
 *
 */
public class DownloadException extends Exception {
	private static final long serialVersionUID = 1L;

	public DownloadException(String message) {
		super(message);
	}
	
	public DownloadException(String message, Exception exception) {
		super(message, exception);
	}

}
