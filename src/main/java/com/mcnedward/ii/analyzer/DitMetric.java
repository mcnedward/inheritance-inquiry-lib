package com.mcnedward.ii.analyzer;

import com.mcnedward.ii.element.JavaElement;

/**
 * Metric for the Depth of Inheritance of a class or interface. Defined by
 * http://www.aivosto.com/project/help/pm-oo-ck.html as the 'maximum inheritance path from the class to the root class'.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public class DitMetric extends Metric {

	public int numberOfInheritedMethods;

	public DitMetric(JavaElement element, int dit, int numberOfInheritedMethods) {
		super(MType.DIT, element, dit);
		this.numberOfInheritedMethods = numberOfInheritedMethods;
	}

}
