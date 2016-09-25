package com.mcnedward.ii.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Modifier;

import com.mcnedward.ii.element.JavaModifier;
import com.mcnedward.ii.exception.ProjectBuildException;

/**
 * @author Edward - Jul 7, 2016
 *
 */
public final class ASTUtils {

	private static final String LIB = "/lib";
	private static final String TARGET = "/target";
	
	public static String[] classPathEntries() {
		return new String[] { "C:/Program Files/Java/jre1.8.0_73/lib/rt.jar" };
	}
	
	public static String[] sourceEntries(String projectPath) throws ProjectBuildException {
		File src = new File(projectPath + "/src");
		File srcMain = new File(projectPath + "/src/main/java");
		File srcTest = new File(projectPath + "/src/test/java");
		if (!src.exists() && !srcMain.exists() && !srcTest.exists())
			throw new ProjectBuildException("The project needs to contain a valid source directory. This should be in \"src\", \"src/main/java\", or \"src/test/java\".");
        List<String> paths = new ArrayList<>();
        paths.add(projectPath);
        if (src.exists())
            paths.add(src.getAbsolutePath());
        if (srcMain.exists())
            paths.add(src.getAbsolutePath());
        if (srcTest.exists())
            paths.add(srcTest.getAbsolutePath());
        return paths.toArray(new String[paths.size()]);
	}
	
	public static String[] encodings(int size) {
        String[] encodings = new String[size];
        for (int i = 0; i < size; i++) {
            encodings[i] = StandardCharsets.UTF_8.toString();
        }
        return encodings;
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
	
	// Adapted from DumpVisitor
	public static List<JavaModifier> decodeModifiers(final int modifiers) {
		List<JavaModifier> modifierList = new ArrayList<>();
		if (Modifier.isPrivate(modifiers)) {
			modifierList.add(JavaModifier.PRIVATE);
		}
		if (Modifier.isProtected(modifiers)) {
			modifierList.add(JavaModifier.PROTECTED);
		}
		if (Modifier.isPublic(modifiers)) {
			modifierList.add(JavaModifier.PUBLIC);
		}
		if (Modifier.isAbstract(modifiers)) {
			modifierList.add(JavaModifier.ABSTRACT);
		}
		if (Modifier.isStatic(modifiers)) {
			modifierList.add(JavaModifier.STATIC);
		}
		if (Modifier.isFinal(modifiers)) {
			modifierList.add(JavaModifier.FINAL);
		}
		if (Modifier.isNative(modifiers)) {
			modifierList.add(JavaModifier.NATIVE);
		}
		if (Modifier.isStrictfp(modifiers)) {
			modifierList.add(JavaModifier.STRICT);
		}
		if (Modifier.isSynchronized(modifiers)) {
			modifierList.add(JavaModifier.SYNCHRONIZED);
		}
		if (Modifier.isTransient(modifiers)) {
			modifierList.add(JavaModifier.TRANSIENT);
		}
		if (Modifier.isVolatile(modifiers)) {
			modifierList.add(JavaModifier.VOLATILE);
		}
		return modifierList;
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
