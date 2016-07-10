package com.mcnedward.ii.element.method;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.mcnedward.ii.element.JavaElement;

/**
 * @author Edward - Jul 10, 2016
 *
 */
public class JavaMethodInvocation {
	
	private JavaMethod mParentMethod;
	private JavaElement mDeclaringClass;
	private MethodInvocation mMethodInvocation;
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
	
	public MethodInvocation getMethodInvocation() {
		return mMethodInvocation;
	}
	
	public void setMethodInvocation(MethodInvocation methodInvocation) {
		mMethodInvocation = methodInvocation;
	}
	
	public IMethodBinding getMethodBinding() {
		return mMethodBinding;
	}
	
	public void setMethodBinding(IMethodBinding methodBinding) {
		mMethodBinding = methodBinding;
	}
}
