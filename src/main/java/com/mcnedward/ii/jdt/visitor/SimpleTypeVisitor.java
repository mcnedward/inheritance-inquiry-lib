package com.mcnedward.ii.jdt.visitor;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleType;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 8, 2016
 *
 */
public class SimpleTypeVisitor extends JavaElementVisitor {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SimpleTypeVisitor.class);

	private boolean mIsInterface;

	public SimpleTypeVisitor(JavaProject project, JavaElement parentElement) {
		super(project, parentElement);
	}

	@Override
	public boolean visit(SimpleType node) {
		String name = node.getName().getFullyQualifiedName();
		ITypeBinding binding = node.resolveBinding();

		// Element is not generic, create if it doesn't exist. Also set the isInterface here
		JavaElement element = findOrCreateElement(name, binding, mIsInterface);
		parentElement().addClassOrInterface(new ClassOrInterfaceElement(element));

		return false;
	}

	public void setIsInterface(boolean isInterface) {
		mIsInterface = isInterface;
	}

}
