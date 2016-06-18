package com.mcnedward.ii.app.element;

import java.io.File;
import java.util.List;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public interface IJavaElement {

	String getName();
	void setName(String name);
	
	String getPackageName();
	
	boolean isInterface();
	void setIsInterface(boolean isInterface);

	void addSuperClass(IJavaElement superClass);
	void addInterface(IJavaElement javaInterface);
	
	List<IJavaElement> getSuperClasses();
	List<IJavaElement> getInterfaces();

	File getSourceFile();
	
	void setSourceFile(File sourceFile);
}
