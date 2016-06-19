package com.mcnedward.ii;

import java.io.File;
import java.util.List;

import com.mcnedward.ii.element.JavaElement;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public class AnalysisResult {

	private File mFile;
	private List<JavaElement> mElements;

	public AnalysisResult(File file, List<JavaElement> elements) {
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
	public List<JavaElement> getElements() {
		return mElements;
	}

}