package com.mcnedward.ii.app.visitor;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import com.mcnedward.ii.app.element.IJavaElement;
import com.mcnedward.ii.app.element.JavaProject;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public abstract class ProjectVisitor extends ASTVisitor {
	private static final Logger logger = Logger.getLogger(ProjectVisitor.class);

	private JavaProject mProject;
	
	public ProjectVisitor(JavaProject project) {
		mProject = project;
	}
	
	protected abstract IJavaElement getJavaElement();

	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().getFullyQualifiedName();
		getJavaElement().setPackageName(packageName);
		logger.info(getJavaElement() + " Package name: " + packageName);
		return true;
	}
	
	/**
	 * @return the project
	 */
	protected JavaProject project() {
		return mProject;
	}

}
