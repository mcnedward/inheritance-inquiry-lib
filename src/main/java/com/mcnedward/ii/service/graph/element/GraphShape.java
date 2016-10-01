package com.mcnedward.ii.service.graph.element;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public enum GraphShape {

	RECT("Rectangle", 0),
    ROUNDED_RECT("Round Rectangle", 1),
	CIRCLE("Circle", 2);
	
	public String graphShape;
    public int graphShapeValue;
	
	GraphShape(String graphShape, int graphShapeValue) {
		this.graphShape = graphShape;
        this.graphShapeValue = graphShapeValue;
	}

	@Override
    public String toString() {
        return graphShape;
    }
}
