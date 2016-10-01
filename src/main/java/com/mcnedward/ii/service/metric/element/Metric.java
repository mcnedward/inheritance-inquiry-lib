package com.mcnedward.ii.service.metric.element;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.service.metric.MetricType;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public abstract class Metric {

	protected MetricType type;
    protected String elementName;
    protected String fullyQualifiedName;
    protected int metric;
    protected boolean isInterface;

	public Metric(MetricType type, JavaElement element, int metric) {
		this.type = type;
		elementName = element.getName();
		fullyQualifiedName = element.getFullyQualifiedName();
		this.metric = metric;
		isInterface = element.isInterface();
	}

    public MetricType getType() {
        return type;
    }

    public String getElementName() {
        return elementName;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public int getMetric() {
        return metric;
    }

    public boolean isInterface() {
        return isInterface;
    }
}
