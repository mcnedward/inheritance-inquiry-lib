package com.mcnedward.ii;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * @author Edward - Jun 22, 2016
 *
 */
public class Analyzer {

	private static final String PROJECT_NAME = "EatingCinci";
	private static final String PROJECT_PATH = "C:/users/edward/dev/workspace/eatingcinci-spring";
	@SuppressWarnings("unused")
	private static final String FILE_PATH = "C:/users/edward/dev/workspace/eatingcinci-spring/src/main/java/com/eatingcinci/app/entity/IEntity.java";

	public void analyze() {
		InterfaceInquiry interfaceInquiry = new InterfaceInquiry();
		interfaceInquiry.buildProject(PROJECT_PATH, PROJECT_NAME, new ProjectBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
			}

			@Override
			public void finished(JavaProject project) {
				calculateDepthOfInheritance(project);
				calculateNumberOfChildren(project);
				System.out.println("Finished.");
			}
		});
	}

	private void calculateDepthOfInheritance(JavaProject project) {
		System.out.println("********** Depth of Inheritance **********");
		List<JavaElement> projectElements = project.getAllElements();
		for (JavaElement element : projectElements) {
			Stack<JavaElement> classStack = new Stack<>();
			if (element.isInterface()) {
				recurseInterfaces(element, classStack);
			} else {
				// Class can only extend one class
				recurseSuperClasses(element, classStack);
			}

			System.out.println(String.format("Depth of inheritance for %s is %s - %s", element, classStack.size(), classStack));
		}
		System.out.println();
	}

	private void recurseSuperClasses(JavaElement javaClass, Stack<JavaElement> classStack) {
		if (javaClass.getSuperClasses().isEmpty())
			return;
		JavaElement elementSuperClass = javaClass.getSuperClasses().get(0);
		classStack.push(elementSuperClass);
		recurseSuperClasses(elementSuperClass, classStack);
	}

	private void recurseInterfaces(JavaElement javaInterface, Stack<JavaElement> classStack) {
		if (javaInterface.getInterfaces().isEmpty())
			return;
		for (JavaElement elementInterface : javaInterface.getInterfaces()) {
			recurseInterfaces(elementInterface, classStack);
			classStack.push(elementInterface);
		}
	}

	private void calculateNumberOfChildren(JavaProject project) {
		System.out.println("********** Number of Children **********");
		for (JavaElement element : project.getAllElements()) {
			List<JavaElement> classChildren = new ArrayList<>();

			// Go through every class and interface in the project to find elements that extend this one
			for (JavaElement projectElement : project.getAllElements()) {
				if (projectElement.isInterface()) {
					if (projectElement.getInterfaces().contains(element))
						classChildren.add(projectElement);
				} else {
					if (projectElement.getSuperClasses().contains(element)) {
						classChildren.add(projectElement);
					}
				}
			}

			System.out.println(String.format("Number of children for %s is %s - %s", element, classChildren.size(), classChildren));
		}
		System.out.println();
	}

	private void findElementsUsingInterfaces(JavaProject project) {
		for (JavaElement element : project.getInterfaces()) {
			for (JavaElement projectClass : project.getClasses()) {
				if (projectClass.getSuperClasses().contains(element)) {
					System.out.println(String.format("Interface %s is implemented by: %s.", element.getName(), projectClass.getName()));
				}
			}

			for (JavaElement projectInterface : project.getInterfaces()) {
				if (projectInterface.getInterfaces().contains(element)) {
					System.out.println(String.format("Interface %s is extended by: %s.", element.getName(), projectInterface.getName()));
				}
			}
		}
	}

	private void findElementsUsingClasses(JavaProject project) {
		for (JavaElement element : project.getClasses()) {
			for (JavaElement projectClass : project.getClasses()) {
				if (projectClass.getSuperClasses().contains(element)) {
					System.out.println(String.format("Class %s is extended by %s.", element.getName(), projectClass.getName()));
				}
			}
		}
	}

	private static void recurse(JavaElement element) {
		for (JavaElement javaInterface : element.getInterfaces()) {
			System.out.println(String.format("%s %s %s", element,
					(element.isInterface() || (!element.isInterface() && !javaInterface.isInterface()) ? "extends" : "implements"), javaInterface));
			if (!javaInterface.getInterfaces().isEmpty()) {
				recurse(javaInterface);
			}
		}
	}
}
