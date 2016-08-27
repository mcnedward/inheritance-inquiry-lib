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
	public String fullyQualifiedName;

	public SolutionMethod(String methodName, String methodSignature, String parentElementName, String elementName, String fullyQualifiedName) {
		this.methodName = methodName;
		this.methodSignature = methodSignature;
		this.parentElementName = parentElementName;
		this.elementName = elementName;
		this.fullyQualifiedName = fullyQualifiedName;
	}

}
