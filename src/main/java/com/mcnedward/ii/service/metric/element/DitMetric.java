package com.mcnedward.ii.service.metric.element;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.service.metric.MType;

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
	
	@Override
	public String toString() {
		return String.format("%s: DIT[%s] - Inherited Methods[%s]", elementName, value, numberOfInheritedMethods);
	}

}
