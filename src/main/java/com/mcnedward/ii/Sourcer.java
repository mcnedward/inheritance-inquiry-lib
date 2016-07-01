package com.mcnedward.ii;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * Allows for parsing files.
 * 
 * @author Edward - Jun 16, 2016
 */
public class Sourcer {

	/**
	 * Parse a directory, and get a list of future promises for parsed files.
	 * 
	 * @param directoryPath
	 *            The path to the directory to parse.
	 * @return A list of Futures that are the .java contents of a directory.
	 * @throws IOException
	 */
	public List<SourcedFile> buildSourceForProject(JavaProject project, ProjectBuildListener listener) throws IOException {
		List<SourcedFile> sourceFiles = new ArrayList<>();
		List<File> files = project.getFiles();
		
		int fileCount = files.size();
		for (int i = 0; i < fileCount; i++) {
			File file = files.get(i);
			
			int progress = (int) (((double) i / fileCount) * 100);
			listener.onProgressChange(String.format("Parsing..."), progress);
			
			sourceFiles.add(sourceFile(file));
		}
		
		return sourceFiles;
	}

	public SourcedFile sourceFile(File file) throws IOException {
		String source = getFileSource(new BufferedReader(new FileReader(file)));
		String fileName = file.getName();
		String name = fileName.substring(0, fileName.indexOf(getFileExtension(fileName)) - 1);
		return new SourcedFile(file, source, name);
	}

	public SourcedFile sourceFile(String filePath) throws IOException {
		return sourceFile(new File(filePath));
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
