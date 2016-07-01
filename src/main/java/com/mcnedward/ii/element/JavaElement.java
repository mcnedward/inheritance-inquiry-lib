package com.mcnedward.ii.element;

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
	private JavaPackage mPackage;
	private List<String> mImports;
	private List<JavaElement> mElements; // All classes or interfaces used by this element
	private List<JavaElement> mTypeArgs;
	private List<JavaMethod> mMethods;
	private List<String> mMissingTypeArgs, mMissingClassOrInterfaceList;
	private List<ClassOrInterfaceHolder> mHolders;
	private boolean mIsInterface;
	private File mSourceFile;
	private boolean mNeedsChecked;

	public JavaElement() {
		mImports = new ArrayList<>();
		mElements = new ArrayList<>();
		mTypeArgs = new ArrayList<>();
		mMethods = new ArrayList<>();
		mMissingTypeArgs = new ArrayList<>();
		mMissingClassOrInterfaceList = new ArrayList<>();
		mHolders = new ArrayList<>();
	}

	public JavaElement(String name, JavaPackage javaPackage) {
		this(name, false); // Default to not an interface
		mPackage = javaPackage;
		mPackageName = javaPackage.getName();
	}

	public JavaElement(String name, boolean isInterface) {
		this();
		mName = name;
		mIsInterface = isInterface;
	}
	
	public JavaElement(String name) {
		this();
		mName = name;
	}

	public void addImport(String importName) {
		mImports.add(importName);
	}

	public void addElement(JavaElement element) {
		mElements.add(element);
	}

	public void addTypeArg(JavaElement element) {
		mTypeArgs.add(element);
	}

	public void addMethod(JavaMethod method) {
		mMethods.add(method);
	}
	
	public void addMissingTypeArg(String typeArg) {
		mMissingTypeArgs.add(typeArg);
	}

	public boolean needsMissingTypeArgChecked() {
		return !mMissingTypeArgs.isEmpty();
	}

	public void addMissingClassOrInterface(String coi) {
		mMissingClassOrInterfaceList.add(coi);
	}

	public boolean needsMissingClassOrInterfaceChecked() {
		return !mMissingClassOrInterfaceList.isEmpty();
	}
	
	public void addHolder(ClassOrInterfaceHolder holder) {
		mHolders.add(holder);
	}

	/**
	 * Gets all of the JavaElements that are super classes.
	 * 
	 * @return The class JavaElements
	 */
	public List<JavaElement> getSuperClasses() {
		List<JavaElement> classes = new ArrayList<>();
		for (JavaElement element : mElements) {
			if (!element.isInterface())
				classes.add(element);
		}
		return classes;
	}

	/**
	 * Gets all of the JavaElements that are interfaces.
	 * 
	 * @return The interface JavaElements
	 */
	public List<JavaElement> getInterfaces() {
		List<JavaElement> interfaces = new ArrayList<>();
		for (JavaElement element : mElements) {
			if (element.isInterface())
				interfaces.add(element);
		}
		return interfaces;
	}
	
	public List<String> getSuperClassesFull() {
		List<String> classes = new ArrayList<>();
		for (JavaElement element : mElements) {
			if (!element.isInterface())
				classes.add(element.getFullyQualifiedName());
		}
		return classes;
	}
	
	public List<String> getInterfacesFull() {
		List<String> interfaces = new ArrayList<>();
		for (JavaElement element : mElements) {
			if (element.isInterface())
				interfaces.add(element.getFullyQualifiedName());
		}
		return interfaces;
	}

	public String getFullyQualifiedName() {
		String out = "";
		if (mPackage != null && !mPackage.getName().equals("")) {
			out = mPackage.getName() + ".";
		}
		return out + mName;
	}

	public void cleanUp() {
		mMissingTypeArgs = new ArrayList<>();
		mMissingClassOrInterfaceList = new ArrayList<>();
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

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}

	public JavaPackage getPackage() {
		return mPackage;
	}

	/**
	 * @param javaPackage
	 *            the javaPackage to set
	 */
	public void setPackage(JavaPackage javaPackage) {
		mPackage = javaPackage;
	}

	public List<String> getImports() {
		return mImports;
	}

	public List<JavaElement> getElements() {
		return mElements;
	}

	public List<JavaElement> getTypeArgs() {
		return mTypeArgs;
	}

	public List<JavaMethod> getMethods() {
		return mMethods;
	}
	
	public List<String> getMissingTypeArgs() {
		return mMissingTypeArgs;
	}

	public List<String> getMissingClassOrInterfaceList() {
		return mMissingClassOrInterfaceList;
	}
	
	public List<ClassOrInterfaceHolder> getHolders() {
		return mHolders;
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

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

}
