package com.mcnedward.ii.element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Stopwatch;


/**
 * @author Edward - Jul 14, 2016
 *
 */
public class JavaSystem {

	private File mSystemFile;
	private String mName;
	private File[] mFiles;
	private List<JavaProject> mProjects;
	private int mProjectsBuiltCount, mProjectsCount;
	private Stopwatch mStopwatch;
	
	public JavaSystem(File systemFile) {
		this(systemFile, systemFile.getName());
	}
	
	public JavaSystem(File systemFile, String fileName) {
		mSystemFile = systemFile;
		mName = fileName;
		mFiles = mSystemFile.listFiles();
		mProjectsCount = mFiles.length;
		mProjects = new ArrayList<>();
		mStopwatch = new Stopwatch();
		mStopwatch.start();
	}

	public void addProject(JavaProject project) {
		mProjects.add(project);
		mProjectsBuiltCount++;
	}
	
	public void stopStopwatch() {
		mStopwatch.stop();
	}
	
	public String getBuildTime() {
		return mStopwatch.toString();
	}
	
	public String getName() {
		return mName;
	}
	
	public File[] getFiles() {
		return mFiles;
	}
	
	public List<JavaProject> getProjects() {
		return mProjects;
	}
	
	public boolean allProjectsBuilt() {
		return mProjectsBuiltCount == mProjectsCount;
	}
	
	@Override
	public String toString() {
		return String.format("%s [%s versions]", mName, mProjects.size());
	}
}
