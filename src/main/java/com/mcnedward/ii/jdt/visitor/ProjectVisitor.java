package com.mcnedward.ii.jdt.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Modifier;

import com.mcnedward.ii.element.JavaModifier;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public abstract class ProjectVisitor extends ASTVisitor {

	private JavaProject mProject;

	public ProjectVisitor(JavaProject project) {
		mProject = project;
	}

	/**
	 * @return the project
	 */
	protected JavaProject project() {
		return mProject;
	}

	// Adapted from DumpVisitor
	protected List<JavaModifier> decodeModifiers(final int modifiers) {
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
}
