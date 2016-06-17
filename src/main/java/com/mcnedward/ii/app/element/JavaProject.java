package com.mcnedward.ii.app.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaProject {

	private String mPath;
	private String mName;
	private List<IJavaElement> mElements;
	
	public JavaProject(String projectPath, String projectName) {
		mPath = projectPath;
		mName = projectName;
		mElements = new ArrayList<>();
	}
	
	public IJavaElement findClass(String className) {
		return find(className, getClasses());
	}
	
	public IJavaElement findInterface(String interfaceName) {
		return find(interfaceName, getInterfaces());
	}

	public IJavaElement find(String elementName) {
		return find(elementName, mElements);
	}
	
	private IJavaElement find(String elementName, List<IJavaElement> elements) {
		for (IJavaElement element : elements) {
			if (element.getName().equals(elementName)) {
				return element;
			}
		}
		return null;
	}
	
	public void addElement(IJavaElement javaElement) {
		mElements.add(javaElement);
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return mPath;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Gets all of the IJavaElements that are classes.
	 * @return The class IJavaElements
	 */
	public List<IJavaElement> getClasses() {
		List<IJavaElement> classes = new ArrayList<>();
		for (IJavaElement element : mElements) {
			if (!element.isInterface())
				classes.add(element);
		}
		return classes;
	}

	/**
	 * Gets all of the IJavaElements that are interfaces.
	 * @return The interface IJavaElements
	 */
	public List<IJavaElement> getInterfaces() {
		List<IJavaElement> interfaces = new ArrayList<>();
		for (IJavaElement element : mElements) {
			if (element.isInterface())
				interfaces.add(element);
		}
		return interfaces;
	}
	
	@Override
	public String toString() {
		return String.format("%s [%s]",	mName, mPath);
	}
}
