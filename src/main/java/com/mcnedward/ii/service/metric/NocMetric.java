package com.mcnedward.ii.service.metric;

import java.util.List;

import com.mcnedward.ii.element.JavaElement;

/**
 * Metric for the Number of Children of a class or interface. Defined by http://www.aivosto.com/project/help/pm-oo-ck.html as the 'number of immediate sub-classes of a class'.
 * @author Edward - Jul 14, 2016
 *
 */
public class NocMetric extends Metric {

	public List<JavaElement> classChildren;
	
	public NocMetric(JavaElement element, int noc, List<JavaElement> classChildren) {
		super(MType.NOC, element, noc);
		this.classChildren = classChildren;
	}

}
