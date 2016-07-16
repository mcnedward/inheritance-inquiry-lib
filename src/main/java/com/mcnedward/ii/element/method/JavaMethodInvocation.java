package com.mcnedward.ii.element.method;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import com.mcnedward.ii.element.JavaElement;

/**
 * Represents a method invocation, inside of another method. This contains the parent {@link JavaMethod}, the {@link JavaElement} where the method
 * is declared, the method invocation (being a {@link Expression}, either {@link MethodInvocation} or
 * {@link SuperMethodInvocation}), and the {@link IMethodBinding} for the method.
 * 
 * @author Edward - Jul 10, 2016
 *
 */
public class JavaMethodInvocation {

	private JavaMethod mParentMethod;
	private JavaElement mDeclaringClass;
	private IMethodBinding mMethodBinding;

	public JavaMethodInvocation(JavaMethod parentMethod, JavaElement declaringClass) {
		mParentMethod = parentMethod;
		mDeclaringClass = declaringClass;
	}

	public JavaMethod getParentMethod() {
		return mParentMethod;
	}

	public JavaElement getDeclaringClass() {
		return mDeclaringClass;
	}

	public IMethodBinding getMethodBinding() {
		return mMethodBinding;
	}

	public void setMethodBinding(IMethodBinding methodBinding) {
		mMethodBinding = methodBinding;
	}

	@Override
	public String toString() {
		return String.format("%s is called in method [%s] in element [%s]", mMethodBinding.getName(), mParentMethod.getName(), mDeclaringClass);
	}
}
