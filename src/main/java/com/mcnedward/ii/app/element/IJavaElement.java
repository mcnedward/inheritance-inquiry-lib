package com.mcnedward.ii.app.element;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public interface IJavaElement {

	String getName();
	String getPackageName();
	void setPackageName(String packageName);
	boolean isInterface();
	void setIsInterface(boolean isInterface);
	/**
	 * @param superClass
	 */
	void addSuperClass(IJavaElement superClass);
	/**
	 * @param javaInterface
	 */
	void addInterface(IJavaElement javaInterface);
	
}
