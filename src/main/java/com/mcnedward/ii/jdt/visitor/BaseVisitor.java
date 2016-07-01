package com.mcnedward.ii.jdt.visitor;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.Modifier;

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
	protected List<Modifier> decodeModifiers(final int modifiers) {
		List<Modifier> modifierList = new ArrayList<>();
		if (ModifierSet.isPrivate(modifiers)) {
			modifierList.add(Modifier.PRIVATE);
		}
		if (ModifierSet.isProtected(modifiers)) {
			modifierList.add(Modifier.PROTECTED);
		}
		if (ModifierSet.isPublic(modifiers)) {
			modifierList.add(Modifier.PUBLIC);
		}
		if (ModifierSet.isAbstract(modifiers)) {
			modifierList.add(Modifier.ABSTRACT);
		}
		if (ModifierSet.isStatic(modifiers)) {
			modifierList.add(Modifier.STATIC);
		}
		if (ModifierSet.isFinal(modifiers)) {
			modifierList.add(Modifier.FINAL);
		}
		if (ModifierSet.isNative(modifiers)) {
			modifierList.add(Modifier.NATIVE);
		}
		if (ModifierSet.isStrictfp(modifiers)) {
			modifierList.add(Modifier.STRICT);
		}
		if (ModifierSet.isSynchronized(modifiers)) {
			modifierList.add(Modifier.SYNCHRONIZED);
		}
		if (ModifierSet.isTransient(modifiers)) {
			modifierList.add(Modifier.TRANSIENT);
		}
		if (ModifierSet.isVolatile(modifiers)) {
			modifierList.add(Modifier.VOLATILE);
		}
		return modifierList;
	}
}
