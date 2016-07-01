package com.mcnedward.ii.jdt.visitor;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;

import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public abstract class ProjectVisitor extends ASTVisitor {
	protected static final Logger logger = Logger.getLogger(ProjectVisitor.class);

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
	
}
