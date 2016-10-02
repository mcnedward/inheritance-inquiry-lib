package com.mcnedward.ii.element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.VersionUtils;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaProject {

	private String mProjectPath;
	private String mName;
	private String mSystemName;
	private String mVersion;
	private File mProjectFile;
	private List<File> mFiles;
	private List<JavaPackage> mPackages;
	private int mClassCount;
	private int mInheritanceCount; // How many times extends is used

	public JavaProject(String projectPath) {
		this(new File(projectPath));
	}

	public JavaProject(File projectFile) {
		mProjectPath = projectFile.getAbsolutePath();
		mSystemName = projectFile.getName();
		mName = projectFile.getName();
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
			IILogger.error(String.format("Could not find the element with the name '%s'.", elementName), e);
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
	 * Finds the Number of Children (NOC) for a JavaElement.
	 * <p>
	 * NOC equals the number of immediate child classes derived from a base class.
	 * (http://www.aivosto.com/project/help/pm-oo-ck.html)
	 * </p>
	 * 
	 * @param element
	 *            The element to find the NOC for.
	 * @return A list of JavaElements that are children of the element given to this method. To find the NOC from this,
	 *         just get the size().
	 */
	public List<JavaElement> findNumberOfChildrenFor(JavaElement element) {
		List<JavaElement> classChildren = new ArrayList<>();

		if (element.isInterface()) {
			return classChildren;
		}

		// Go through every class and interface in the project to find elements that extend this one
		for (JavaElement projectElement : getAllElements()) {
			if (projectElement.isInterface())
				continue;

			if (projectElement.getSuperClasses().contains(element)) {
				classChildren.add(projectElement);
			}
		}
		return classChildren;
	}

	/**
	 * Calculates the total Number of Children and Weighted Method for an element. Elements that have many methods and
	 * children can be a sign of poor design, since the children elements will inherit many methods and will become more
	 * complex.
	 * 
	 * @param element
	 *            The element to inspect.
	 * @return The NOC and WMC for the element.
	 */
	public int findNOCAndWMCFor(JavaElement element) {
		int wmc = element.getMethods().size();
		int noc = findNumberOfChildrenFor(element).size();
		int total = wmc + noc;
		return total;
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
		return findOrCreateElement(packageName, elementName, false);
	}

	public JavaElement findOrCreateElement(String packageName, String elementName, boolean isInterface) {
		JavaElement element = null;
		if (packageName == null) {
			// Use default package
			packageName = "default";
		}
		JavaPackage javaPackage = findPackage(packageName);
		if (javaPackage == null) {
			// Package does not exist yet, so create it
			// Add the class to the package, and the package to the project
			javaPackage = new JavaPackage(packageName);
			element = new JavaElement(elementName, javaPackage, isInterface);
			javaPackage.addElement(element);
			addPackage(javaPackage);
		} else {
			// Find the class in the package
			element = javaPackage.find(elementName);
			if (element == null) {
				element = new JavaElement(elementName, javaPackage, isInterface);
				javaPackage.addElement(element);
			}
		}
		return element;
	}

	/**
	 * Finds the Depth of Inheritance Tree (DIT) for a JavaElement.
	 * <p>
	 * DIT equals the maximum inheritance path from the class to the root class.
	 * (http://www.aivosto.com/project/help/pm-oo-ck.html)
	 * </p>
	 * 
	 * @param element
	 *            The element to find the DIT for.
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

	/**
	 * Calculates the number of inherited methods of a JavaElement, based on their DIT.
	 * 
	 * @param element
	 *            The element to find the number of inherited methods for.
	 * @return The number of inherited methods.
	 */
	public int findNumberOfInheritedMethodsFor(JavaElement element) {
		Stack<JavaElement> classStack = findDepthOfInheritanceTreeFor(element);
		if (classStack.size() == 0)
			return 0;
		int numOfInheritedMethods = 0;
		for (JavaElement child : classStack) {
			numOfInheritedMethods += child.getMethods().size();
		}
		return numOfInheritedMethods;
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
				if (!element.isInterface() && !element.hasDiamonds()) {
					mClasses.add(element);
				}
			}
		}
		return mClasses;
	}

	// Cache the project fully qualified names
    private List<String> mFullyQualifiedElementNames;
	public List<String> getProjectFullyQualifiedElementNames() {
        if (mFullyQualifiedElementNames != null && !mFullyQualifiedElementNames.isEmpty())
            return mFullyQualifiedElementNames;
        mFullyQualifiedElementNames = new ArrayList<>();
        for (JavaElement element : getAllElements()) {
            if (element.hasDiamonds()) continue;
            mFullyQualifiedElementNames.add(element.getFullyQualifiedName());
        }
        return mFullyQualifiedElementNames;
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
				if (element.isInterface() && !element.hasDiamonds()) {
					mInterfaces.add(element);
				}
			}
		}
		return mInterfaces;
	}

	private List<JavaElement> mCachedElements;

	public List<JavaElement> getAllElements() {
		if (mCachedElements != null)
			return mCachedElements;
		mCachedElements = new ArrayList<>(getClasses());
		mCachedElements.addAll(getInterfaces());
		return mCachedElements;
	}

	public void incrementInheritanceUse() {
		mInheritanceCount++;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return mProjectPath;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	public String getSystemName() {
		return mSystemName;
	}

	public String getVersion() {
		return mVersion;
	}

	public void setVersion(String version) {
		mVersion = version;
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

	public int getClassCount() {
		if (mClassCount == 0) {
			List<JavaElement> allElements = getAllElements();
			for (JavaElement element : allElements) {
				if (!element.isInterface())
					mClassCount++;
			}
		}
		return mClassCount;
	}

	public int getInheritanceCount() {
		return mInheritanceCount;
	}

	@Override
	public String toString() {
		return mName;
	}
}
