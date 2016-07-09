package com.mcnedward.ii.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.mcnedward.ii.element.JavaModifier;

/**
 * @author Edward - Jul 8, 2016
 *
 */
public class MethodUtils {
	
	private static final String METHOD_DELIMITER = ", ";
	private static final String SPACE = " ";

	public static String getMethodSignature(IMethodBinding binding) {
		List<JavaModifier> javaModifiers = ASTUtils.decodeModifiers(binding.getModifiers());
		String modifiers = String.join(SPACE, javaModifiers.stream()
				.map(modifier -> modifier.name)
				.toArray(String[]::new));
		
		String returnType;
		ITypeBinding returnTypeBinding = binding.getReturnType();
		if (returnTypeBinding == null) {
			returnType = "void";
		} else {
			returnType = returnTypeBinding.getQualifiedName();
		}
		
		String methodName = binding.getName();
		
		String parameters = getParameters(binding.getParameterTypes());
		
		return String.format("%s %s %s(%s)", modifiers, returnType, methodName, parameters); 
	}
	
	private static String getParameters(ITypeBinding[] bindings) {
		if (bindings.length == 0) {
			return "";
		}
		
		List<String> parameters = new ArrayList<>();
		for (ITypeBinding binding : bindings) {
			parameters.add(binding.getQualifiedName());
		}
		return String.join(METHOD_DELIMITER, parameters);
	}

}
