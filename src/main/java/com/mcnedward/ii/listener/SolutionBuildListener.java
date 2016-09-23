package com.mcnedward.ii.listener;

import com.mcnedward.ii.element.JavaSolution;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public interface SolutionBuildListener extends BuildListener {
	void finished(JavaSolution solution);
}
