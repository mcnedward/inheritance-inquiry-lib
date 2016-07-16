package com.mcnedward.ii.jdt.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.utils.ASTUtils;
import com.mcnedward.ii.utils.MethodUtils;

/**
 * Visitor for setting up a method inside of a {@link JavaElement}.
 * 
 * @author Edward - Jun 24, 2016
 *
 */
public class MethodVisitor extends JavaElementVisitor {
	private static final Logger logger = Logger.getLogger(MethodVisitor.class);

	public MethodVisitor(JavaProject project, JavaElement parentElement) {
		super(project, parentElement);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (node.isConstructor())
			return false;

		JavaMethod method = new JavaMethod();
		parentElement().addMethod(method);
		method.setName(node.getName().getFullyQualifiedName());

		method.setReturnType(node.getReturnType2().toString());
		method.setModifiers(ASTUtils.decodeModifiers(node.getModifiers()));

		@SuppressWarnings("unchecked")
		List<ASTNode> parameters = node.parameters();
		for (ASTNode param : parameters) {
			param.accept(new MethodParameterVisitor(project(), method));
		}

		IMethodBinding binding = node.resolveBinding();
		// Save the binding for use later
		// NOTE: I think the keeping a refernce to bindings prevent the ASTParser from being garbage collected, but as
		// long as the entire JavaProject is not kept around for too long, this should be fine
		method.setMethodBinding(binding);
		String signature = MethodUtils.getMethodSignature(binding);
		method.setSignature(signature);
		logger.debug(method.getSignature());

		// Visit all of the method invocations inside this method
		node.accept(new MethodInvocationVisitor(project(), parentElement(), method));

		return super.visit(node);
	}

}
