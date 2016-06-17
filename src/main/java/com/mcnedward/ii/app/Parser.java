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
	public List<ParsedFile> parseDirectory(String directoryPath) throws IOException {
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
	public List<ParsedFile> parseDirectory(File directory) throws IOException {
		List<ParsedFile> files = new ArrayList<>();
		handleDirectory(directory, files);
		return files;
	}

	public ParsedFile parseFile(File file) throws IOException {
		String source = getFileSource(new BufferedReader(new FileReader(file)));
		String fileName = file.getName();
		String name = fileName.substring(0, fileName.indexOf(getFileExtension(fileName)) - 1);
		return new ParsedFile(file, source, name);
	}

	public ParsedFile parseFile(String filePath) throws IOException {
		return parseFile(new File(filePath));
	}

	/**
	 * Recursively move through a directory to parse the file contents.
	 * 
	 * @param directory
	 *            The directory to parse.
	 * @param files
	 * @throws IOException
	 */
	private void handleDirectory(File directory, List<ParsedFile> files) throws IOException {
		File[] directoryFiles = directory.listFiles();
		for (File file : directoryFiles) {
			if (file.isDirectory()) {
				handleDirectory(file, files);
			}
			if (file.isFile()) {
				String fileExtension = getFileExtension(file.getName());
				// Only need to parse java files
				if (fileExtension != null && fileExtension.equals("java")) {
					files.add(parseFile(file));
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

	private String getFileSource(BufferedReader reader) throws IOException {
		StringBuilder fileData = new StringBuilder(16384);
		char[] buffer = new char[16384];
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
