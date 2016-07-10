package com.mcnedward.ii.javaparser.visitor;

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.element.method.MethodParameter;

/**
 * @author Edward - Jun 24, 2016
 *
 */
public class MethodVisitor extends BaseVisitor<JavaMethod> {
	
	public MethodVisitor(JavaProject project) {
		super(project);
	}
	
	@Override
	public void visit(MethodDeclaration node, JavaMethod method) {
		method.setName(node.getName());
		method.setReturnType(node.getType().toString());
		method.setModifiers(decodeModifiers(node.getModifiers()));
		
		for (Parameter param : node.getParameters()) {
			param.accept(this, method);
		}
	}
	
	@Override
	public void visit(Parameter p, JavaMethod method) {
		MethodParameter methodParameter = new MethodParameter();
		methodParameter.setParameterName(p.getId().getName());
		methodParameter.setParameterType(p.getType().toString());
		method.addParameter(methodParameter);
	}

	@Override
	public void visit(AnnotationDeclaration n, JavaMethod arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}

	@Override
	public void visit(AnnotationMemberDeclaration n, JavaMethod arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}

	@Override
	public void visit(NormalAnnotationExpr n, JavaMethod arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}

	@Override
	public void visit(SingleMemberAnnotationExpr n, JavaMethod arg) {
		// TODO Auto-generated method stub
		super.visit(n, arg);
	}

}
