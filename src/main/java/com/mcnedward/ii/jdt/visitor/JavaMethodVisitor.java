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

/**
 * @author Edward - Jun 24, 2016
 *
 */
public class JavaMethodVisitor extends ProjectVisitor {
	private static final Logger logger = Logger.getLogger(JavaMethodVisitor.class);
	
	private JavaElement mParentElement;
	
	public JavaMethodVisitor(JavaProject project, JavaElement parentElement) {
		super(project);
		mParentElement = parentElement;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		if (node.isConstructor()) return false;
		
		JavaMethod method = new JavaMethod();
		mParentElement.addMethod(method);
		method.setName(node.getName().getFullyQualifiedName());
		
		method.setReturnType(node.getReturnType2().toString());
		method.setModifiers(decodeModifiers(node.getModifiers()));
		
		List<ASTNode> parameters = node.parameters();
		for (ASTNode param : parameters) {
			param.accept(new JavaMethodParameterVisitor(project(), method));
		}
		
		IMethodBinding binding = node.resolveBinding();
		logger.info(binding);
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

//	@Override
//	public boolean visit(ParameterizedType node) {
//		List<SimpleType> typeArguments = node.typeArguments();
//		for (SimpleType type : typeArguments) {
//			MethodParameter param = new MethodParameter();
//		}
//
//		MethodHolder holder = new MethodHolder(node.getType().toString());
//		if (holder != null) {
//			for (SimpleType t : typeArguments) {
//				String typeName = t.toString();
//				holder.addTypeArg(typeName);
//			}
//		}
//		return super.visit(node);
//	}
}
