package com.mcnedward.ii.element;

import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.element.generic.GenericParameter;

/**
 * @author Edward - Jul 9, 2016
 *
 */
public class ClassOrInterfaceElement {

	private JavaElement mCoiElement;					// The class or interface element
	private List<JavaElement> mTypeArgs;				// Type args used for this class or interface
	private List<GenericParameter> mGenericTypeArgs;	// Generic type args used for this class or interface
	
	public ClassOrInterfaceElement(JavaElement element) {
		mCoiElement = element;
		mTypeArgs = new ArrayList<>();
		mGenericTypeArgs = new ArrayList<>();
	}
	
	public JavaElement getElement() {
		return mCoiElement;
	}
	
	public List<JavaElement> getTypeArgs() {
		return mTypeArgs;
	}
	
	public void addTypeArg(JavaElement typeArg) {
		mTypeArgs.add(typeArg);
	}
	
	public void addGenericTypeArg(GenericParameter param) {
		mGenericTypeArgs.add(param);
	}
	
	public boolean isInterface() {
		return mCoiElement.isInterface();
	}
	
	@Override
	public String toString() {
		return String.format("%s - Type Args: %s - Generic Type Args: %s", mCoiElement.getName(), mTypeArgs, mGenericTypeArgs);
	}
	
}
