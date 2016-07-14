package com.mcnedward.ii.listener;

import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public interface ProjectBuildListener extends BuildListener {
	void finished(JavaProject project);
}
