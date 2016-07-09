package com.mcnedward.ii.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.mcnedward.ii.element.JavaModifier;
import com.mcnedward.ii.element.generic.ResolvedGeneric;

/**
 * @author Edward - Jul 8, 2016
 *
 */
public class MethodUtils {
	
	private static final String METHOD_DELIMITER = ", ";
	private static final String SPACE = " ";

	public static String getMethodSignature(IMethodBinding binding) {
		String modifiers = getModifiers(binding);
		String returnType = getReturnType(binding);
		String methodName = binding.getName();
		String parameters = getParameters(binding.getParameterTypes());
		
		return String.format("%s %s %s(%s)", modifiers, returnType, methodName, parameters); 
	}
	
	public static String getMethodSignatureWithGenerics(IMethodBinding binding, List<ResolvedGeneric> generics) {
		String modifiers = getModifiers(binding);
		String returnType = getReturnType(binding);
		String methodName = binding.getName();
		String parameters = getParametersWithGenerics(binding.getParameterTypes(), generics);
		
		return String.format("%s %s %s(%s)", modifiers, returnType, methodName, parameters);
	}
	
	private static String getModifiers(IMethodBinding binding) {
		List<JavaModifier> javaModifiers = ASTUtils.decodeModifiers(binding.getModifiers());
		String modifiers = String.join(SPACE, javaModifiers.stream()
				.map(modifier -> modifier.name)
				.toArray(String[]::new));
		return modifiers;
	}
	
	private static String getReturnType(IMethodBinding binding) {
		String returnType;
		ITypeBinding returnTypeBinding = binding.getReturnType();
		if (returnTypeBinding == null) {
			returnType = "void";
		} else {
			returnType = returnTypeBinding.getQualifiedName();
		}
		return returnType;
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

	private static String getParametersWithGenerics(ITypeBinding[] bindings, List<ResolvedGeneric> generics) {
		if (bindings.length == 0) {
			return "";
		}
		
		List<String> parameters = new ArrayList<>();
		for (ITypeBinding binding : bindings) {
			String paramName = binding.getQualifiedName();
			
			for (ResolvedGeneric resolvedGeneric : generics) {
				if (resolvedGeneric.getGeneric().getName().equals(paramName)) {
					// Found a generic method type argument, so replace with the correct element
					paramName = resolvedGeneric.getElement().getFullyQualifiedName();
					break;
				}
			}

			parameters.add(paramName);
		}
		return String.join(METHOD_DELIMITER, parameters);
	}
	
}
