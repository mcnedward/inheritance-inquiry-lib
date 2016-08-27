package com.mcnedward.ii.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Modifier;

import com.mcnedward.ii.element.JavaModifier;

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
