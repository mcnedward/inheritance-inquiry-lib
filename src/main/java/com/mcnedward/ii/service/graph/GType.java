package com.mcnedward.ii.service.graph;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public enum GType {

	OMETHODS("Overridden Methods"),
	EMETHODS("Extended Methods"),
	TREE("Hierarchy Tree");
	
	public String graphType;
	
	GType(String graphType) {
		this.graphType = graphType;
	}
}
