package com.mcnedward.ii;

import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 22, 2016
 *
 */
public class Analyzer {

	public void analyze(JavaProject project) {
		calculateDepthOfInheritance(project);
		calculateNumberOfChildren(project);
		calculateWeightedMethodsPerClass(project);
		findClassesWithHighNOCAndWMC(project);
	}

	private void calculateDepthOfInheritance(JavaProject project) {
		System.out.println("********** Depth of Inheritance **********");
		List<JavaElement> projectElements = project.getAllElements();
		for (JavaElement element : projectElements) {
			Stack<JavaElement> classStack = project.findDepthOfInheritanceTreeFor(element);
			if (classStack.size() > 0) {
				int numOfInheritedMethods = 0;
				for (JavaElement child : classStack) {
					numOfInheritedMethods += child.getMethods().size();
				}
				System.out.println(String.format("Depth of inheritance for %s is %s - %s\nNumber of inherited methods: %s", element,
						classStack.size(), classStack, numOfInheritedMethods));
			}
		}
		System.out.println();
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
			int wmc = element.getMethods().size();
			int noc = project.findNumberOfChildrenFor(element).size();

			int total = wmc + noc;

			if (total > 30)
				System.out.println(String.format(
						"%s has a high NOC and WMC [%s]. Considering a refactor to separate to reduce the number of methods inherited to children.",
						element, total));
		}
		System.out.println();
	}
}
