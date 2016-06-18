package com.mcnedward.ii.app.element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private List<IJavaElement> mElements;

	public JavaProject(String projectPath, String projectName) {
		mPath = projectPath;
		mName = projectName;
		mElements = new ArrayList<>();
		buildFile();
	}

	public IJavaElement find(String elementName) {
		try {
			for (IJavaElement element : mElements) {
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

	/**
	 * Adds an interface to the project if it does not exist, or updates the existing interface.
	 * 
	 * @param interfaceToSaveOrUpdate
	 * @return
	 */
	// public IJavaElement saveOrUpdateInterface(IJavaElement interfaceToSaveOrUpdate) {
	// IJavaElement original = findInterface(interfaceToSaveOrUpdate.getName());
	// if (original == null) {
	// // Just save the interface
	// original = interfaceToSaveOrUpdate;
	// addElement(original);
	// } else {
	// // Interface exists, so update
	// original.setPackageName(interfaceToSaveOrUpdate.getPackageName());
	// original.setSuperClasses(interfaceToSaveOrUpdate.getSuperClasses());
	// original.setInterfaces(interfaceToSaveOrUpdate.getInterfaces());
	// }
	// return original;
	// }

	private void buildFile() {
		mProjectFile = new File(mPath);
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

	public void addElement(IJavaElement javaElement) {
		logger.info("Adding: " + javaElement);
		mElements.add(javaElement);
	}

	/**
	 * Gets all of the IJavaElements that are classes.
	 * 
	 * @return The class IJavaElements
	 */
	public List<IJavaElement> getClasses() {
		List<IJavaElement> classes = new ArrayList<>();
		for (IJavaElement element : mElements) {
			if (!element.isInterface())
				classes.add(element);
		}
		return classes;
	}

	/**
	 * Gets all of the IJavaElements that are interfaces.
	 * 
	 * @return The interface IJavaElements
	 */
	public List<IJavaElement> getInterfaces() {
		List<IJavaElement> interfaces = new ArrayList<>();
		for (IJavaElement element : mElements) {
			if (element.isInterface())
				interfaces.add(element);
		}
		return interfaces;
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

	@Override
	public String toString() {
		return String.format("%s [%s]", mName, mPath);
	}
}
