package com.mcnedward.ii.jdt.visitor;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.element.method.JavaMethodInvocation;

/**
 * Visitor used for defining all of the {@link MethodInvocation}s or {@link SuperMethodInvocation}s in a method.
 * 
 * @author Edward - Jun 24, 2016
 *
 */
public class MethodInvocationVisitor extends JavaElementVisitor {

	private JavaMethod mMethod;

	public MethodInvocationVisitor(JavaProject project, JavaElement parentElement, JavaMethod method) {
		super(project, parentElement);
		mMethod = method;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		return handleInvocation(node.resolveMethodBinding(), node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		return handleInvocation(node.resolveMethodBinding(), node);
	}

	private boolean handleInvocation(IMethodBinding methodBinding, Expression node) {
		if (methodBinding == null)
			return false;

		ITypeBinding declaringClassBinding = methodBinding.getDeclaringClass();

		JavaElement declaringClass = findOrCreateElement(declaringClassBinding.getName(), declaringClassBinding);
		JavaMethodInvocation invocation = new JavaMethodInvocation(mMethod, declaringClass);
		invocation.setMethodBinding(methodBinding);
		mMethod.addMethodInvocation(invocation);

		return false;
	}

}
