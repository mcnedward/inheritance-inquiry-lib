package com.mcnedward.ii.listener;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public interface BuildListener {
	void onProgressChange(String message, int progress);
	void onBuildError(String message, Exception exception);
}
