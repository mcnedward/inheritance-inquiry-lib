package com.mcnedward.ii.jdt.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleType;

import com.mcnedward.ii.element.GenericParameter;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 8, 2016
 *
 */
public class SimpleTypeVisitor extends JavaElementVisitor {
	private static final Logger logger = Logger.getLogger(SimpleTypeVisitor.class);

	private List<GenericParameter> mGenericArgs;
	private boolean mIsInterface;

	public SimpleTypeVisitor(JavaProject project, JavaElement parentElement) {
		super(project, parentElement);
	}

	@Override
	public boolean visit(SimpleType node) {
		String name = node.getName().getFullyQualifiedName();
		ITypeBinding binding = node.resolveBinding();

		if (mGenericArgs != null) {
			for (GenericParameter param : mGenericArgs) {
				if (param.getName().equals(name)) {
					// This type param being visited is a generic type parameter created in the top level parent element
					element().addGenericTypeArg(param);
					return false;
				}
			}
		}

		// Element is not generic, create if it doesn't exist. Also set the isInterface here
		JavaElement element = findOrCreateElement(name, binding);
		element.setIsInterface(mIsInterface);

		return false;
	}

	public void setGenericArgs(List<GenericParameter> args) {
		mGenericArgs = args;
	}
	
	public void setIsInterface(boolean isInterface) {
		mIsInterface = isInterface;
	}

}
