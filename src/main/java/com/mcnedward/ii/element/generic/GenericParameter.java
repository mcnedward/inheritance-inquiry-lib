package com.mcnedward.ii.element.generic;

import com.mcnedward.ii.element.JavaElement;

/**
 * A generic type parameter. This contains the name of the type parameter, and the class or interface that this generic
 * is used on.
 * 
 * @author Edward - Jul 8, 2016
 *
 */
public class GenericParameter {

	private String mName;
	private JavaElement mElement;

	public GenericParameter(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public JavaElement getElement() {
		return mElement;
	}

	public void setElement(JavaElement element) {
		mElement = element;
	}

	@Override
	public String toString() {
		return mName;
	}

}
