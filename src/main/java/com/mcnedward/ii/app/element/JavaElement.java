package com.mcnedward.ii.app.element;

import java.io.File;
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
	private List<IJavaElement> mInterfaces;	// extends
	private boolean mIsInterface;
	private File mSourceFile;
	
	public JavaElement() {
		mSuperClasses = new ArrayList<>();
		mInterfaces = new ArrayList<>();
	}
	
	public JavaElement(String name) {
		this(name, false);	// Default to not an interface
	}
	
	public JavaElement(String name, boolean isInterface) {
		this();
		mName = name;
		mIsInterface = isInterface;
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
	 * @param name
	 */
	@Override
	public void setName(String name) {
		mName = name;
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
	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * @return the superClasses
	 */
	@Override
	public List<IJavaElement> getSuperClasses() {
		return mSuperClasses;
	}

	/**
	 * @param superClasses the superClasses to set
	 */
	public void setSuperClasses(List<IJavaElement> superClasses) {
		mSuperClasses = superClasses;
	}
	
	/**
	 * @return the interfaces
	 */
	@Override
	public List<IJavaElement> getInterfaces() {
		return mInterfaces;
	}

	/**
	 * @param interfaces the interfaces to set
	 */
	public void setInterfaces(List<IJavaElement> interfaces) {
		mInterfaces = interfaces;
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
	public File getSourceFile() {
		return mSourceFile;
	}
	
	@Override
	public void setSourceFile(File sourceFile) {
		mSourceFile = sourceFile;
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
