package com.mcnedward.ii.element;

/**
 * @author Edward - Jul 8, 2016
 *
 */
public class GenericParameter {

	private String mName;
	private JavaElement mSuperClass;
	
	public GenericParameter(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}

	public JavaElement getSuperClass() {
		return mSuperClass;
	}
	
	public void setSuperClass(JavaElement superClass) {
		mSuperClass = superClass;
	}
	
	@Override
	public String toString() {
		return mName;
	}
	
}
