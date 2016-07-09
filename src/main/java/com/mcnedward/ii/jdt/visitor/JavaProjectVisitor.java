package com.mcnedward.ii.jdt.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;

import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public abstract class JavaProjectVisitor extends ASTVisitor {

	private JavaProject mProject;

	public JavaProjectVisitor(JavaProject project) {
		mProject = project;
	}

	/**
	 * @return the project
	 */
	protected JavaProject project() {
		return mProject;
	}
}
