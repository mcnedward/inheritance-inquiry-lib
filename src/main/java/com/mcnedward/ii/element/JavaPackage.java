package com.mcnedward.ii.element;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public class JavaPackage {
	private static final Logger logger = Logger.getLogger(JavaPackage.class);
	
	private String mName;
	private Set<JavaElement> mElements;

	public JavaPackage(String name) {
		mName = name;
		mElements = new HashSet<>();
	}
	
	public JavaElement find(String elementName) {
		try {
			for (JavaElement element : mElements) {
				if (element.getName().equals(elementName)) {
					return element;
				}
			}
			return null;
		} catch (NullPointerException e) {
			logger.error(String.format("Could not find the element with the name '%s'.", elementName));
			throw e;
		}
	}
	
	public void addElement(JavaElement element) {
		mElements.add(element);
	}

	/**
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return the elements
	 */
	public Set<JavaElement> getElements() {
		return mElements;
	}

	/**
	 * @param elements
	 *            the elements to set
	 */
	public void setElements(Set<JavaElement> elements) {
		mElements = elements;
	}
	
	@Override
	public String toString() {
		return String.format("%s [%s elements]", mName, mElements.size());
	}

}
