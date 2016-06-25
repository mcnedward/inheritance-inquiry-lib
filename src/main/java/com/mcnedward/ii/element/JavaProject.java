package com.mcnedward.ii.element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaProject {
	protected static final Logger logger = Logger.getLogger(JavaProject.class);

	private String mPath;
	private String mName;
	private File mProjectFile;
	private List<File> mFiles;
	private List<JavaPackage> mPackages;

	public JavaProject(String projectPath, String projectName) {
		mPath = projectPath;
		mName = projectName;
		mProjectFile = new File(mPath);
		mPackages = new ArrayList<>();
		buildFile();
	}
	
	public JavaProject(File projectFile, String projectName) {
		mPath = projectFile.getAbsolutePath();
		mName = projectName;
		mProjectFile = projectFile;
		mPackages = new ArrayList<>();
		buildFile();
	}

	public JavaElement find(String elementName) {
		try {
			for (JavaPackage javaPackage : mPackages) {
				for (JavaElement element : javaPackage.getElements()) {
					if (element.getName().equals(elementName)) {
						return element;
					}
				}
			}
			return null;
		} catch (NullPointerException e) {
			logger.error(String.format("Could not find the element with the name '%s'.", elementName));
			throw e;
		}
	}

	public JavaPackage findPackage(String packageName) {
		for (JavaPackage javaPackage : mPackages) {
			if (javaPackage.getName().equals(packageName)) {
				return javaPackage;
			}
		}
		return null;
	}

	/**
	 * Finds the Depth of Inheritance Tree (DIT) for a JavaElement.
	 * <p>DIT equals the maximum inheritance path from the class to the root class. (http://www.aivosto.com/project/help/pm-oo-ck.html)</p>
	 * @param element The element to find the DIT for.
	 * @return A Stack of JavaElements that are a part of the inheritance tree for the element given to this method.
	 */
	public Stack<JavaElement> findDepthOfInheritanceTreeFor(JavaElement element) {
		Stack<JavaElement> classStack = new Stack<>();
		if (element.isInterface()) {
			recurseInterfaces(element, classStack);
		} else {
			// Class can only extend one class
			recurseSuperClasses(element, classStack);
		}
		return classStack;
	}

	private void recurseSuperClasses(JavaElement javaClass, Stack<JavaElement> classStack) {
		if (javaClass.getSuperClasses().isEmpty())
			return;
		JavaElement elementSuperClass = javaClass.getSuperClasses().get(0);
		classStack.push(elementSuperClass);
		recurseSuperClasses(elementSuperClass, classStack);
	}

	private void recurseInterfaces(JavaElement javaInterface, Stack<JavaElement> classStack) {
		if (javaInterface.getInterfaces().isEmpty())
			return;
		for (JavaElement elementInterface : javaInterface.getInterfaces()) {
			recurseInterfaces(elementInterface, classStack);
			classStack.push(elementInterface);
		}
	}

	/**
	 * Finds the Number of Children (NOC) for a JavaElement.
	 * <p>
	 * NOC equals the number of immediate child classes derived from a base class.
	 * (http://www.aivosto.com/project/help/pm-oo-ck.html)
	 * </p>
	 * 
	 * @param element
	 *            The element to find the NOC for.
	 * @return A list of JavaElements that are children of the element given to this method. To find the NOC
	 *         from this, just get the size().
	 */
	public List<JavaElement> findNumberOfChildrenFor(JavaElement element) {
		List<JavaElement> classChildren = new ArrayList<>();

		// Go through every class and interface in the project to find elements that extend this one
		for (JavaElement projectElement : getAllElements()) {
			if (projectElement.isInterface()) {
				if (projectElement.getInterfaces().contains(element))
					classChildren.add(projectElement);
			} else {
				if (projectElement.getSuperClasses().contains(element)) {
					classChildren.add(projectElement);
				}
			}
		}
		return classChildren;
	}

	public void addPackage(JavaPackage javaPackage) {
		mPackages.add(javaPackage);
	}

	private void buildFile() {
		mFiles = new ArrayList<>();
		if (mProjectFile.isDirectory()) {
			buildDirectory(mProjectFile.listFiles());
		} else {
			mFiles.add(mProjectFile);
		}
	}

	private void buildDirectory(File[] directory) {
		for (File file : directory) {
			if (file.isDirectory())
				buildDirectory(file.listFiles());
			if (file.isFile()) {
				if (file.getAbsolutePath().endsWith(".java")) {
					mFiles.add(file);
				}
			}
		}
	}

	public JavaElement findOrCreateElement(String packageName, String elementName) {
		JavaElement element = null;
		JavaPackage javaPackage = findPackage(packageName);
		if (javaPackage == null) {
			// Package does not exist, so class cannot either.
			// Add the class to the package, and the package to the project
			javaPackage = new JavaPackage(packageName);
			element = new JavaElement(elementName, javaPackage);
			javaPackage.addElement(element);
			addPackage(javaPackage);
			element.setNeedsInterfaceStatusChecked(true);
		} else {
			// Find the class in the package
			element = javaPackage.find(elementName);
			if (element == null) {
				element = new JavaElement(elementName, javaPackage);
				javaPackage.addElement(element);
				element.setNeedsInterfaceStatusChecked(true);
			}
		}
		return element;
	}

	// Cache the search for classes
	private List<JavaElement> mClasses;

	/**
	 * Gets all of the JavaElements that are classes.
	 * 
	 * @return The class JavaElements
	 */
	public List<JavaElement> getClasses() {
		if (mClasses != null && !mClasses.isEmpty()) {
			return mClasses;
		}
		mClasses = new ArrayList<>();
		for (JavaPackage javaPackage : mPackages) {
			for (JavaElement element : javaPackage.getElements()) {
				if (!element.isInterface()) {
					mClasses.add(element);
				}
			}
		}
		return mClasses;
	}

	// Cache the search for interfaces
	private List<JavaElement> mInterfaces;

	/**
	 * Gets all of the JavaElements that are interfaces.
	 * 
	 * @return The interface JavaElements
	 */
	public List<JavaElement> getInterfaces() {
		if (mInterfaces != null && !mInterfaces.isEmpty()) {
			return mInterfaces;
		}
		mInterfaces = new ArrayList<>();
		for (JavaPackage javaPackage : mPackages) {
			for (JavaElement element : javaPackage.getElements()) {
				if (element.isInterface()) {
					mInterfaces.add(element);
				}
			}
		}
		return mInterfaces;
	}

	public List<JavaElement> getAllElements() {
		List<JavaElement> elements = new ArrayList<>(getClasses());
		elements.addAll(getInterfaces());
		return elements;
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
	 * @return the projectFile
	 */
	public File getProjectFile() {
		return mProjectFile;
	}

	/**
	 * @return the files
	 */
	public List<File> getFiles() {
		return mFiles;
	}

	/**
	 * @return the packages
	 */
	public List<JavaPackage> getPackages() {
		return mPackages;
	}

	/**
	 * @param packages
	 *            the packages to set
	 */
	public void setPackages(List<JavaPackage> packages) {
		this.mPackages = packages;
	}

	@Override
	public String toString() {
		return String.format("%s [%s]", mName, mPath);
	}
}
