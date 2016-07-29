package com.mcnedward.ii.service.metric.element;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.service.metric.MType;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public abstract class Metric {

	public MType type;
	public String elementName;
	public String fullyQualifiedName;
	public int metric;

	public Metric(MType type, JavaElement element, int metric) {
		this.type = type;
		elementName = element.getName();
		fullyQualifiedName = element.getFullyQualifiedName();
		this.metric = metric;
	}
}
