package com.mcnedward.ii.service.metric.element;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.service.metric.MType;
import com.mcnedward.ii.service.metric.Metric;

/**
 * Metric for the Weighted Method Count of a class. Defined by http://www.aivosto.com/project/help/pm-oo-ck.html as the 'number of methods defined in class'.
 * @author Edward - Jul 14, 2016
 *
 */
public class WmcMetric extends Metric {

	public WmcMetric(JavaElement element, int wmc) {
		super(MType.WMC, element, wmc);
	}

}
