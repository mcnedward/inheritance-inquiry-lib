package com.mcnedward.ii.app.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaElement implements IJavaElement {
	
	private String mName;
	private String mPackageName;
	private List<IJavaElement> mSuperClasses;	// extends
	private List<IJavaElement> mInterfaces;
	private boolean mIsInterface;
	
	public JavaElement(String className) {
		mName = className;
		mSuperClasses = new ArrayList<>();
		mInterfaces = new ArrayList<>();
	}
	
	@Override
	public void addSuperClass(IJavaElement superClass) {
		mSuperClasses.add(superClass);
	}
	
	@Override
	public void addInterface(IJavaElement javaInterface) {
		mInterfaces.add(javaInterface);
	}
	
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * @return the packageName
	 */
	@Override
	public String getPackageName() {
		return mPackageName;
	}

	/**
	 * @param packageName the packageName to set
	 */
	@Override
	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * @return the superClasses
	 */
	public List<IJavaElement> getSuperClasses() {
		return mSuperClasses;
	}

	/**
	 * @param superClasses the superClasses to set
	 */
	public void setSuperClasses(List<IJavaElement> superClasses) {
		mSuperClasses = superClasses;
	}
	
	@Override
	public boolean isInterface() {
		return mIsInterface;
	}
	
	@Override
	public void setIsInterface(boolean isInterface) {
		mIsInterface = isInterface;
	}

	@Override
	public String toString() {
		String out = "";
		if (mPackageName != null && !mPackageName.equals("")) {
			out = mPackageName + ".";
		}
		return out + mName;
	}

}
