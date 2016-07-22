package com.mcnedward.ii.element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.element.generic.GenericParameter;
import com.mcnedward.ii.element.method.JavaMethod;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaElement {

	private String mName;
	private String mPackageName;
	private JavaPackage mPackage;
	private List<String> mImports;
	private List<ClassOrInterfaceElement> mClassOrInterfaceElements;	// All classes or interfaces used by this element
	private List<JavaElement> mTypeArgs;
	private List<GenericParameter> mGenericTypeArgs;
	private List<JavaMethod> mMethods;
	private List<String> mMissingTypeArgs, mMissingClassOrInterfaceList;
	private boolean mIsInterface;
	private File mSourceFile;

	public JavaElement() {
		mImports = new ArrayList<>();
		mClassOrInterfaceElements = new ArrayList<>();
		mTypeArgs = new ArrayList<>();
		mGenericTypeArgs = new ArrayList<>();
		mMethods = new ArrayList<>();
		mMissingTypeArgs = new ArrayList<>();
		mMissingClassOrInterfaceList = new ArrayList<>();
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

	public void addClassOrInterface(ClassOrInterfaceElement element) {
		mClassOrInterfaceElements.add(element);
	}
	
	public void addTypeArg(JavaElement element) {
		mTypeArgs.add(element);
	}

	public void addGenericTypeArg(GenericParameter param) {
		mGenericTypeArgs.add(param);
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

	public List<ClassOrInterfaceElement> getClassOrInterfaceElements() {
		return mClassOrInterfaceElements;
	}
	
	private List<ClassOrInterfaceElement> mCachedSuperClassCOIs;
	public List<ClassOrInterfaceElement> getSuperClassCois() {
		if (mCachedSuperClassCOIs != null) return mCachedSuperClassCOIs;
		mCachedSuperClassCOIs = new ArrayList<>();
		for (ClassOrInterfaceElement e : mClassOrInterfaceElements) {
			if (!e.isInterface())
				mCachedSuperClassCOIs.add(e);
		}
		return mCachedSuperClassCOIs;
	}
	
	private List<ClassOrInterfaceElement> mCachedInterfaceCOIs;
	public List<ClassOrInterfaceElement> getInterfaceCois() {
		if (mCachedInterfaceCOIs != null) return mCachedInterfaceCOIs;
		mCachedInterfaceCOIs = new ArrayList<>();
		for (ClassOrInterfaceElement e : mClassOrInterfaceElements) {
			if (e.isInterface())
				mCachedInterfaceCOIs.add(e);
		}
		return mCachedInterfaceCOIs;
	}
	
	private List<JavaElement> mCachedSuperClasses;
	public List<JavaElement> getSuperClasses() {
		if (mCachedSuperClasses != null) return mCachedSuperClasses;
		mCachedSuperClasses = new ArrayList<>();
		for (ClassOrInterfaceElement e : mClassOrInterfaceElements) {
			if (!e.isInterface())
				mCachedSuperClasses.add(e.getElement());
		}
		return mCachedSuperClasses;
	}
	
	private List<JavaElement> mCachedInterfaces;
	public List<JavaElement> getInterfaces() {
		if (mCachedInterfaces != null) return mCachedInterfaces;
		mCachedInterfaces = new ArrayList<>();
		for (ClassOrInterfaceElement e : mClassOrInterfaceElements) {
			if (e.isInterface())
				mCachedInterfaces.add(e.getElement());
		}
		return mCachedInterfaces;
	}
	
	private int mWmc = 0;
	public int getWeightedMethodCount() {
		if (mWmc == 0) {
			mWmc = mMethods.size();
		}
		return mWmc;
	}
	
	public List<JavaElement> getTypeArgs() {
		return mTypeArgs;
	}
	
	public List<GenericParameter> getGenericTypeArgs() {
		return mGenericTypeArgs;
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

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

}
