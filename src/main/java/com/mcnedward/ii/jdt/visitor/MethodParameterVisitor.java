package com.mcnedward.ii.jdt.visitor;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;

import com.mcnedward.ii.element.JavaMethod;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.MethodParameter;

/**
 * @author Edward - Jun 24, 2016
 *
 */
public class MethodParameterVisitor extends JavaProjectVisitor {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MethodParameterVisitor.class);
	
	private JavaMethod mMethod;
	
	public MethodParameterVisitor(JavaProject project, JavaMethod method) {
		super(project);
		mMethod = method;
	}

	@Override
	public boolean visit(ParameterizedType node) {
		MethodParameter parameter = new MethodParameter();
		parameter.setParameterType(node.toString());
		mMethod.addParameter(parameter);
		return super.visit(node);
	}

	@Override
	public boolean visit(PrimitiveType node) {
		MethodParameter parameter = new MethodParameter();
		parameter.setParameterType(node.toString());
		mMethod.addParameter(parameter);
		return super.visit(node);
	}

	@Override
	public boolean visit(QualifiedType node) {
		MethodParameter parameter = new MethodParameter();
		parameter.setParameterType(node.toString());
		mMethod.addParameter(parameter);
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleType node) {
		MethodParameter parameter = new MethodParameter();
		parameter.setParameterType(node.toString());
		mMethod.addParameter(parameter);
		return super.visit(node);
	}
	
}
