package com.mcnedward.ii;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaMethod;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.generic.GenericParameter;
import com.mcnedward.ii.element.generic.ResolvedGeneric;
import com.mcnedward.ii.utils.MethodUtils;

/**
 * @author Edward - Jun 22, 2016
 *
 */
public class Analyzer {
	private static final Logger logger = Logger.getLogger(Analyzer.class);

	public void analyze(JavaProject project) {
		calculateDepthOfInheritance(project);
		calculateNumberOfChildren(project);
		calculateWeightedMethodsPerClass(project);
		findClassesWithHighNOCAndWMC(project);
	}

	public static void calculateDepthOfInheritance(JavaProject project) {
		List<JavaElement> projectElements = project.getAllElements();
		for (JavaElement element : projectElements) {
			Stack<JavaElement> classStack = project.findDepthOfInheritanceTreeFor(element);
			int DOT = project.findNumberOfInheritedMethodsFor(element);
			System.out.println(String.format("Depth of inheritance for %s is %s - %s\nNumber of inherited methods: %s", element, classStack.size(),
					classStack, DOT));
		}
	}

	public static void calculateOverridenMethods(JavaProject project) {
		for (JavaElement child : project.getClasses()) {
			if (child.getSuperClasses().isEmpty()) continue;
			JavaElement parent = child.getSuperClasses().get(0);
			
			List<GenericParameter> generics = parent.getGenericTypeArgs();
			
			List<String> parentMethodSignatures = new ArrayList<>();
			if (generics.isEmpty()) {
				// If no generics in parent declaration, just get all the methods
				for (JavaMethod parentMethod : parent.getMethods())
					parentMethodSignatures.add(parentMethod.getSignature());
			} else {
				// Otherwise, convert the parent method signatures to replace the generic type args with the correct type.
				// First, resolve the generics for the parent class, using the child class
				// The generate the new, correct method signatures
				
				List<ResolvedGeneric> resolvedGenerics = new ArrayList<>();
				ClassOrInterfaceElement superClass = child.getSuperClassCois().get(0);
				List<JavaElement> superClassTypeArgs = superClass.getTypeArgs();
				for (int i = 0; i < superClassTypeArgs.size(); i++) {
					// The generic parameter and the superclass type used to represent that generic SHOULD have the same index
					JavaElement superClassTypeArg = superClassTypeArgs.get(i);
					GenericParameter genericParameter = generics.get(i);
					
					ResolvedGeneric resolvedGeneric = new ResolvedGeneric(genericParameter, superClassTypeArg);
					resolvedGenerics.add(resolvedGeneric);
				}
				
				for (JavaMethod parentMethod : parent.getMethods()) {
					String methodSignature = MethodUtils.getMethodSignatureWithGenerics(parentMethod.getMethodBinding(), resolvedGenerics);
					parentMethodSignatures.add(methodSignature);
					logger.debug(String.format("Update method signature for class %s:\n%s\n%s", parent, parentMethod.getSignature(), methodSignature));
				}
			}
			
			for (JavaMethod childMethod : child.getMethods()) {
				String childSignature = childMethod.getSignature();
				
				for (String parentSignature : parentMethodSignatures) {
					if (childSignature.equals(parentSignature)) {
						logger.info(String.format("Element %s is overriding method %s defined in parent class %s.", child, childSignature, parent));
					}
				}
			}
		}
	}
	
	private void calculateNumberOfChildren(JavaProject project) {
		System.out.println("********** Number of Children **********");
		for (JavaElement element : project.getAllElements()) {
			List<JavaElement> classChildren = project.findNumberOfChildrenFor(element);
			System.out.println(String.format("Number of children for %s is %s - %s", element, classChildren.size(), classChildren));
		}
		System.out.println();
	}

	private void calculateWeightedMethodsPerClass(JavaProject project) {
		System.out.println("********** Weighted Methods Per Class **********");
		for (JavaElement element : project.getAllElements()) {
			System.out.println(String.format("Weighted methods for %s is %s", element, element.getMethods().size()));
		}
		System.out.println();
	}

	private void findClassesWithHighNOCAndWMC(JavaProject project) {
		System.out.println("********** Classes With High NOC & WMC **********");
		for (JavaElement element : project.getAllElements()) {
			int total = project.findNOCAndWMCFor(element);
			if (total > 30)
				System.out.println(String.format(
						"%s has a high NOC and WMC [%s]. Considering a refactor to separate to reduce the number of methods inherited to children.",
						element, total));
		}
		System.out.println();
	}
}
