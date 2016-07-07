package com.mcnedward.ii.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Edward - Jul 7, 2016
 *
 */
public class ASTUtils {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ASTUtils.class);

	private static final String LIB = "/lib";
	private static final String TARGET = "/target";
	
	public static String[] classPathEntries() {
		return new String[] { "C:/Program Files/Java/jre1.8.0_73/lib/rt.jar" };
	}
	
	public static String[] sourceEntries(String projectPath) {
		return new String[] {
				projectPath,
				projectPath + "/src", 
				projectPath + "/src/main/java",
				projectPath + "/src/test/java"
		};
	}
	
	public static String[] encodings() {
		return new String[] { StandardCharsets.UTF_8.toString(), StandardCharsets.UTF_8.toString(), StandardCharsets.UTF_8.toString(), StandardCharsets.UTF_8.toString() };
	}
	
	public static String[] getClassPathEntries(String projectPath) {
		File libFolder = new File(projectPath + LIB);
		File targetFolder = new File(projectPath + TARGET);
		
		List<String> classPaths = new ArrayList<String>();
		if (libFolder.exists())
			getClassPathEntriesForFolder(libFolder, classPaths);
		if (targetFolder.exists())
			getClassPathEntriesForFolder(targetFolder, classPaths);
		
		classPaths.add("C:/Program Files/Java/jre1.8.0_73/lib/rt.jar");
		
		return classPaths.toArray(new String[classPaths.size()]);
	}
	
	private static void getClassPathEntriesForFolder(File folder, List<String> classPaths) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				getClassPathEntriesForFolder(file, classPaths);
			} else {
				if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".class")) {
					classPaths.add(file.getAbsolutePath());
				}
			}
		}
	}
	
}
