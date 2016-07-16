package com.mcnedward.ii.service.metric;

import com.mcnedward.ii.element.JavaElement;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public abstract class Metric {

	public MType type;
	public JavaElement element;
	public int metric;

	public Metric(MType type, JavaElement element, int metric) {
		this.type = type;
		this.element = element;
		this.metric = metric;
	}
}
