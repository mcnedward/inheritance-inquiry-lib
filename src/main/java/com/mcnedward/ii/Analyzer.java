package com.mcnedward.ii;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaMethod;
import com.mcnedward.ii.element.JavaProject;

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
			
			for (JavaMethod childMethod : child.getMethods()) {
				String childSignature = childMethod.getSignature();
				for (JavaMethod parentMethod : parent.getMethods()) {
					String parentSignature = parentMethod.getSignature();
					
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
