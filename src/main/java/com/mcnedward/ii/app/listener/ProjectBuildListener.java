package com.mcnedward.ii.app.listener;

import com.mcnedward.ii.app.element.JavaProject;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public interface ProjectBuildListener {
	void onProgressChange(String message, int progress);
	void finished(JavaProject project);
}
