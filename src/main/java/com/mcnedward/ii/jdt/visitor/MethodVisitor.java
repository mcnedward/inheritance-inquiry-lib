package com.mcnedward.ii.jdt.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaMethod;
import com.mcnedward.ii.element.JavaProject;
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
		element().addMethod(method);
		method.setName(node.getName().getFullyQualifiedName());

		method.setReturnType(node.getReturnType2().toString());
		method.setModifiers(ASTUtils.decodeModifiers(node.getModifiers()));

		@SuppressWarnings("unchecked")
		List<ASTNode> parameters = node.parameters();
		for (ASTNode param : parameters) {
			param.accept(new MethodParameterVisitor(project(), method));
		}

		IMethodBinding binding = node.resolveBinding();
		String signature = MethodUtils.getMethodSignature(binding);
		method.setSignature(signature);
		logger.debug(method.getSignature());

		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	// @Override
	// public boolean visit(ParameterizedType node) {
	// List<SimpleType> typeArguments = node.typeArguments();
	// for (SimpleType type : typeArguments) {
	// MethodParameter param = new MethodParameter();
	// }
	//
	// MethodHolder holder = new MethodHolder(node.getType().toString());
	// if (holder != null) {
	// for (SimpleType t : typeArguments) {
	// String typeName = t.toString();
	// holder.addTypeArg(typeName);
	// }
	// }
	// return super.visit(node);
	// }
}
