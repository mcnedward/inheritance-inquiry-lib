package com.mcnedward.ii.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for parsing files.
 * 
 * @author Edward - Jun 16, 2016
 */
public class Parser {

	/**
	 * Parse a directory, and get a list of future promises for parsed files.
	 * 
	 * @param directoryPath
	 *            The path to the directory to parse.
	 * @return A list of Futures that are the .java contents of a directory.
	 * @throws IOException
	 */
	public List<String> parseDirectory(String directoryPath) throws IOException {
		File directory = new File(directoryPath);
		return parseDirectory(directory);
	}

	/**
	 * Parse a directory, and get a list of future promises for parsed files.
	 * 
	 * @param directory
	 *            The directory to parse.
	 * @return A list of Futures that are the .java contents of a directory.
	 * @throws IOException
	 */
	public List<String> parseDirectory(File directory) throws IOException {
		List<String> files = new ArrayList<>();
		handleDirectory(directory, files);
		return files;
	}

	/**
	 * Recursively move through a directory to parse the file contents.
	 * 
	 * @param directory
	 *            The directory to parse.
	 * @param tasks
	 *            A list of callable tasks that are the .java contents of this directory.
	 * @throws IOException
	 */
	private void handleDirectory(File directory, List<String> tasks) throws IOException {
		File[] directoryFiles = directory.listFiles();
		for (File file : directoryFiles) {
			if (file.isDirectory()) {
				handleDirectory(file, tasks);
			}
			if (file.isFile()) {
				String fileExtension = getFileExtension(file.getName());
				// Only need to parse java files
				if (fileExtension != null && fileExtension.equals("java")) {
					tasks.add(parseFile(file));
				}
			}
		}
	}

	private String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			return fileName.substring(index + 1);
		}
		return null;
	}

	private String parseFile(File file) throws IOException {
		return getFileData(new BufferedReader(new FileReader(file)));
	}

	@SuppressWarnings("unused")
	private String parseFile(String filePath) throws IOException {
		return getFileData(new BufferedReader(new FileReader(filePath)));
	}

	private String getFileData(BufferedReader reader) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		char[] buffer = new char[10];
		int numberRead = 0;
		while ((numberRead = reader.read(buffer)) != -1) {
			String readData = String.valueOf(buffer, 0, numberRead);
			fileData.append(readData);
			buffer = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}
