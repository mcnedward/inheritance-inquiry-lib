package com.mcnedward.ii.jdt.visitor;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeParameter;

import com.mcnedward.ii.element.GenericParameter;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 8, 2016
 *
 */
public class TypeParameterVisitor extends JavaElementVisitor {

	public TypeParameterVisitor(JavaProject project, JavaElement parentElement) {
		super(project, parentElement);
	}
	
	@Override
	public boolean visit(TypeParameter node) {
		String name = node.getName().getFullyQualifiedName();
		ITypeBinding binding = node.resolveBinding();
		
		ITypeBinding superClassBinding = binding.getSuperclass();
		String superClassName = superClassBinding.getName();
		
		JavaElement superClass = findOrCreateElement(superClassName, superClassBinding);
		
		GenericParameter param = new GenericParameter(name);
		param.setSuperClass(superClass);
		
		element().addGenericTypeArg(param);
		
		return false;
	}

}
