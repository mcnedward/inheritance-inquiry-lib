package com.mcnedward.ii.utils;

import java.io.File;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class SourcedFile {

	private File mFile;
	private String mSource;
	private String mName;

	/**
	 * Holder for a File and the String source (contents of the file).
	 * @param file
	 * @param source
	 * @param name
	 */
	public SourcedFile(File file, String source, String name) {
		mFile = file;
		mSource = source;
		mName = name;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return mFile;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return mSource;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return mFile != null ? mFile.getName() : "Parsed File (no file set)";
	}
}
