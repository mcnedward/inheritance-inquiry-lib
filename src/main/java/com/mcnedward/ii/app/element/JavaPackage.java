package com.mcnedward.ii.app.element;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public class JavaPackage {
	private static final Logger logger = Logger.getLogger(JavaPackage.class);
	
	private String mName;
	private List<JavaElement> mElements;

	public JavaPackage(String name) {
		mName = name;
		mElements = new ArrayList<>();
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
	public List<JavaElement> getElements() {
		return mElements;
	}

	/**
	 * @param elements
	 *            the elements to set
	 */
	public void setElements(List<JavaElement> elements) {
		mElements = elements;
	}
	
	@Override
	public String toString() {
		return String.format("%s [%s elements]", mName, mElements.size());
	}

}
