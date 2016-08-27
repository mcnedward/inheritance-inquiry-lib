package com.mcnedward.ii.service.metric;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public enum MType {

	DIT("Depth of Inheritance Tree"),
	NOC("Number of Children"),
	WMC("Weighted Method Count"),
	OM("Overridden Methods"),
	EM("Extended Methods");
	
	public String metricName;
	
	MType(String metricName) {
		this.metricName = metricName;
	}
	
}
