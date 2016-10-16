package com.mcnedward.ii.listener;

import com.mcnedward.ii.element.JavaSolution;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public interface SolutionBuildListener extends BuildListener {
	
	/**
	 * Notifies when the JavaSolution is built successfully.
	 * @param solution The {@link JavaSolution}
	 */
	void finished(JavaSolution solution);
}
