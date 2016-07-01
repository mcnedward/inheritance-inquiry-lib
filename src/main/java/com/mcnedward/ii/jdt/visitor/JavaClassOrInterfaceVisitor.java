package com.mcnedward.ii.jdt.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;

import com.mcnedward.ii.element.ClassOrInterfaceHolder;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaClassOrInterfaceVisitor extends ProjectVisitor {
	protected static final Logger logger = Logger.getLogger(JavaClassOrInterfaceVisitor.class);

	private JavaElement mParentElement;
	private boolean mIsInterface;
	private ClassOrInterfaceHolder mHolder;

	public JavaClassOrInterfaceVisitor(JavaProject project, JavaElement parentElement) {
		super(project);
		mParentElement = parentElement;
	}

	@Override
	public boolean visit(ParameterizedType node) {
		List<SimpleType> typeArguments = node.typeArguments();

		ClassOrInterfaceHolder holder = holder(node.getType().toString());
		if (holder != null) {
			for (SimpleType t : typeArguments) {
				String typeName = t.toString();
				holder.addTypeArg(typeName);
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleName node) {
		String name = node.getFullyQualifiedName();
		holder(name);
		return super.visit(node);
	}

	private ClassOrInterfaceHolder holder(String name) {
		if (mHolder != null)
			return mHolder;
		mHolder = new ClassOrInterfaceHolder(name, mIsInterface);
		mParentElement.addHolder(mHolder);
		return mHolder;
	}

	public void setIsInterface(boolean isInterface) {
		mIsInterface = isInterface;
	}

	public void reset() {
		mHolder = null;
	}
}
