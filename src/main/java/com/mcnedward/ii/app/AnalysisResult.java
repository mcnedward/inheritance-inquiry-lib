package com.mcnedward.ii.app;

import java.io.File;
import java.util.List;

import com.mcnedward.ii.app.element.IJavaElement;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public class AnalysisResult {

	private File mFile;
	private List<IJavaElement> mElements;

	public AnalysisResult(File file, List<IJavaElement> elements) {
		mFile = file;
		mElements = elements;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return mFile;
	}

	/**
	 * @return The IJavaElements
	 */
	public List<IJavaElement> getElements() {
		return mElements;
	}

}