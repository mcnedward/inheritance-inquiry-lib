package com.mcnedward.ii.listener;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public interface BuildListener {
	/**
	 * Notifies on progress updates.
	 * @param message The progress update message.
	 * @param progress The current progress.
	 */
	void onProgressChange(String message, int progress);
	
	/**
	 * Notifies when a build error occurs.
	 * @param message The build error message.
	 * @param exception The exception that caused the error.
	 */
	void onBuildError(String message, Exception exception);
}
