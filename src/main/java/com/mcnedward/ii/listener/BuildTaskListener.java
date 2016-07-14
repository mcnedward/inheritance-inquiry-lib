package com.mcnedward.ii.listener;

import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public interface BuildTaskListener extends BuildListener {

	void onTaskComplete(JavaProject project);
	
}
