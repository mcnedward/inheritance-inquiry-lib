package com.mcnedward.ii.visitor;

import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mcnedward.ii.element.JavaProject;

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
	
}
