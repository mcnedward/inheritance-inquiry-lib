package com.mcnedward.ii.app.element;

import java.io.File;
import java.util.List;

/**
 * TODO This could probably be deleted, since we don't currently have any class beside JavaElement that implements
 * IJavaElement.
 * 
 * @author Edward - Jun 16, 2016
 *
 */
public interface IJavaElement {

	String getName();

	void setName(String name);

	String getPackageName();

	boolean isInterface();

	void setIsInterface(boolean isInterface);

	void addElement(IJavaElement element);

	void addSuperClass(IJavaElement superClass);

	void addInterface(IJavaElement javaInterface);

	List<IJavaElement> getSuperClasses();

	List<IJavaElement> getInterfaces();

	List<IJavaElement> getElements();

	File getSourceFile();

	void setSourceFile(File sourceFile);

	/**
	 * When a JavaElement is created by means other than being visited by a ClassOrInterfaceDeclaration node, then it
	 * needs to be checked at the end of the build to ensure that it is set to the correct state (isInterface).
	 * 
	 * @return
	 */
	boolean needsInterfaceStatusChecked();

	void setNeedsInterfaceStatusChecked(boolean needsInterfaceStatusChecked);

	/**
	 * When a JavaElement extends or implements another JavaElement, and the package and element for that extends or
	 * implements could not be found.
	 * 
	 * @return
	 */
	boolean needsMissingClassOrInterfaceChecked();

	void setNeedsMissingClassOrInterfaceChecked(boolean needsMissingClassOrInterfaceChecked);
}
