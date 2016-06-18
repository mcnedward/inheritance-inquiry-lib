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
	private List<JavaPackage> mPackages;
	private List<JavaElement> mElements;

	public JavaProject(String projectPath, String projectName) {
		mPath = projectPath;
		mName = projectName;
		mPackages = new ArrayList<>();
		mElements = new ArrayList<>();
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

	public void addPackage(JavaPackage javaPackage) {
		mPackages.add(javaPackage);
	}

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

	/**
	 * Gets all of the JavaElements that are classes.
	 * 
	 * @return The class JavaElements
	 */
	public List<JavaElement> getClasses() {
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
