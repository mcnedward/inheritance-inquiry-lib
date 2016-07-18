package com.mcnedward.ii.service.graph.element;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public class SolutionMethod {
	
	public String methodName;
	public String methodSignature;
	public String parentElementName;
	public String elementName;

	public SolutionMethod(String methodName, String methodSignature, String parentElementName, String elementName) {
		this.methodName = methodName;
		this.methodSignature = methodSignature;
		this.parentElementName = parentElementName;
		this.elementName = elementName;
	}

}
