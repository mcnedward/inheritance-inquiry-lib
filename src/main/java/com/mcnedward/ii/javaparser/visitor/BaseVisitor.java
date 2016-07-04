package com.mcnedward.ii.javaparser.visitor;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaModifier;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public abstract class BaseVisitor<T> extends VoidVisitorAdapter<T> {

	private JavaProject mProject;

	public BaseVisitor(JavaProject project) {
		mProject = project;
	}

	protected JavaProject project() {
		return mProject;
	}

	// Adapted from DumpVisitor
	protected List<JavaModifier> decodeModifiers(final int modifiers) {
		List<JavaModifier> modifierList = new ArrayList<>();
		if (ModifierSet.isPrivate(modifiers)) {
			modifierList.add(JavaModifier.PRIVATE);
		}
		if (ModifierSet.isProtected(modifiers)) {
			modifierList.add(JavaModifier.PROTECTED);
		}
		if (ModifierSet.isPublic(modifiers)) {
			modifierList.add(JavaModifier.PUBLIC);
		}
		if (ModifierSet.isAbstract(modifiers)) {
			modifierList.add(JavaModifier.ABSTRACT);
		}
		if (ModifierSet.isStatic(modifiers)) {
			modifierList.add(JavaModifier.STATIC);
		}
		if (ModifierSet.isFinal(modifiers)) {
			modifierList.add(JavaModifier.FINAL);
		}
		if (ModifierSet.isNative(modifiers)) {
			modifierList.add(JavaModifier.NATIVE);
		}
		if (ModifierSet.isStrictfp(modifiers)) {
			modifierList.add(JavaModifier.STRICT);
		}
		if (ModifierSet.isSynchronized(modifiers)) {
			modifierList.add(JavaModifier.SYNCHRONIZED);
		}
		if (ModifierSet.isTransient(modifiers)) {
			modifierList.add(JavaModifier.TRANSIENT);
		}
		if (ModifierSet.isVolatile(modifiers)) {
			modifierList.add(JavaModifier.VOLATILE);
		}
		return modifierList;
	}
}
