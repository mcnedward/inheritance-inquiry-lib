package com.mcnedward.ii.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward - Jun 24, 2016
 *
 */
public class BaseObject {
	
	protected List<Modifier> mModifiers;
	protected String mName;
	protected boolean isAbstract;

	public BaseObject() {
		mModifiers = new ArrayList<>();
	}

	/**
	 * @return the modifiers
	 */
	public List<Modifier> getModifiers() {
		return mModifiers;
	}

	/**
	 * @param modifiers
	 *            the modifiers to set
	 */
	public void setModifiers(List<Modifier> modifiers) {
		mModifiers = modifiers;
		isAbstract = modifiers.contains(Modifier.ABSTRACT);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		mName = name;
	}
	
	/**
	 * @return the isAbstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * @param isAbstract the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

}
