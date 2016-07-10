package com.mcnedward.ii.jdt.visitor;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.element.method.JavaMethodInvocation;

/**
 * 
 * @author Edward - Jun 24, 2016
 *
 */
public class MethodInvocationVisitor extends JavaElementVisitor {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MethodInvocationVisitor.class);

	private JavaMethod mMethod;
	
	public MethodInvocationVisitor(JavaProject project, JavaElement parentElement, JavaMethod method) {
		super(project, parentElement);
		mMethod = method;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding methodBinding = node.resolveMethodBinding();
		ITypeBinding declaringClassBinding = methodBinding.getDeclaringClass();
		
		JavaElement declaringClass = findOrCreateElement(declaringClassBinding.getName(), declaringClassBinding);
		JavaMethodInvocation invocation = new JavaMethodInvocation(mMethod, declaringClass);
		invocation.setMethodBinding(methodBinding);
		invocation.setMethodInvocation(node);
		mMethod.addMethodInvocation(invocation);
		
		return false;
	}
	
}
