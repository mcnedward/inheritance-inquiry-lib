package com.mcnedward.ii.app.element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaElement {
	
	private String mName;
	private String mPackageName;
	private List<JavaElement> mSuperClasses;	// extends
	private List<JavaElement> mInterfaces;		// interfaces
	private List<JavaElement> mElements;		// All classes or interfaces used by this element
	private List<String> mMissingClassOrInterfaceList;
	private boolean mIsInterface;
	private File mSourceFile;
	private boolean mNeedsChecked, mNeedsMissingClassOrInterfaceChecked;
	
	public JavaElement() {
		mSuperClasses = new ArrayList<>();
		mInterfaces = new ArrayList<>();
		mElements = new ArrayList<>();
		mMissingClassOrInterfaceList = new ArrayList<>();
	}
	
	public JavaElement(String name) {
		this(name, false);	// Default to not an interface
	}
	
	public JavaElement(String name, boolean isInterface) {
		this();
		mName = name;
		mIsInterface = isInterface;
	}
	
	public void addSuperClass(JavaElement superClass) {
		mSuperClasses.add(superClass);
	}
	
	public void addInterface(JavaElement javaInterface) {
		mInterfaces.add(javaInterface);
	}
	
	public void addElement(JavaElement element) {
		mElements.add(element);
	}
	
	public void addMissingClassOrInterface(String coi) {
		mMissingClassOrInterfaceList.add(coi);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		mName = name;
	}
	
	/**
	 * @return the packageName
	 */
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
	public List<JavaElement> getSuperClasses() {
		return mSuperClasses;
	}

	/**
	 * @param superClasses the superClasses to set
	 */
	public void setSuperClasses(List<JavaElement> superClasses) {
		mSuperClasses = superClasses;
	}
	
	/**
	 * @return the interfaces
	 */
	public List<JavaElement> getInterfaces() {
		return mInterfaces;
	}

	/**
	 * @param interfaces the interfaces to set
	 */
	public void setInterfaces(List<JavaElement> interfaces) {
		mInterfaces = interfaces;
	}
	
	public List<JavaElement> getElements() {
		return mElements;
	}
	
	public List<String> getMissingClassOrInterfaceList() {
		return mMissingClassOrInterfaceList;
	}
	
	public boolean isInterface() {
		return mIsInterface;
	}
	
	public void setIsInterface(boolean isInterface) {
		mIsInterface = isInterface;
	}
	
	public File getSourceFile() {
		return mSourceFile;
	}
	
	public void setSourceFile(File sourceFile) {
		mSourceFile = sourceFile;
	}
	
	/**
	 * @return
	 */
	public boolean needsInterfaceStatusChecked() {
		return mNeedsChecked;
	}

	/**
	 * @param needsChecked
	 */
	public void setNeedsInterfaceStatusChecked(boolean needsChecked) {
		mNeedsChecked = needsChecked;
	}

	/**
	 * @return
	 */
	public boolean needsMissingClassOrInterfaceChecked() {
		return mNeedsMissingClassOrInterfaceChecked;
	}

	/**
	 * @param needsMissinClassOrInterfaceChecked
	 */
	public void setNeedsMissingClassOrInterfaceChecked(boolean needsMissinClassOrInterfaceChecked) {
		mNeedsMissingClassOrInterfaceChecked = needsMissinClassOrInterfaceChecked;
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
