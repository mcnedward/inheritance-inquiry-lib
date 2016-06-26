package com.mcnedward.ii.listener;

import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public interface ProjectBuildListener {
	void onProgressChange(String message, int progress);
	void finished(JavaProject project);
	void onBuildError(String message, Exception exception);
}
