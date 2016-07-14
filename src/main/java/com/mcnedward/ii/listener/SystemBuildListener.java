package com.mcnedward.ii.listener;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public interface SystemBuildListener extends BuildListener {
	void onProjectBuilt(JavaProject project);
	void onAllProjectsBuilt(JavaSystem system);
}
