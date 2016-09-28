package com.mcnedward.ii.service.graph.element;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public enum GraphShape {

	RECT("Rectangle"),
    ROUNDED_RECT("Round Rectangle"),
	CIRCLE("Circle");
	
	public String graphShape;
	
	GraphShape(String graphShape) {
		this.graphShape = graphShape;
	}

	@Override
    public String toString() {
        return graphShape;
    }
}
