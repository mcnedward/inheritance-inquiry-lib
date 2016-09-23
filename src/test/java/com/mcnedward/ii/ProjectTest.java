package com.mcnedward.ii;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaPackage;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public abstract class ProjectTest {

	public JavaProject createProject(boolean isInterface) {
		JavaElement base = new JavaElement("base");
		ClassOrInterfaceElement baseCoi = new ClassOrInterfaceElement(base);
		JavaElement parent1 = new JavaElement("parent1");
		ClassOrInterfaceElement parent1Coi = new ClassOrInterfaceElement(parent1);
		JavaElement parent2 = new JavaElement("parent2");
		ClassOrInterfaceElement parent2Coi = new ClassOrInterfaceElement(parent2);
		JavaElement element = new JavaElement("element");

		base.setIsInterface(isInterface);
		parent1.setIsInterface(isInterface);
		parent2.setIsInterface(isInterface);
		element.setIsInterface(isInterface);

		element.addClassOrInterface(parent2Coi);
		parent2.addClassOrInterface(parent1Coi);
		parent1.addClassOrInterface(baseCoi);

		JavaPackage javaPackage = new JavaPackage("default");
		javaPackage.addElement(element);
		javaPackage.addElement(parent1);
		javaPackage.addElement(parent2);
		javaPackage.addElement(base);
		
		JavaProject project = new JavaProject("");
		project.addPackage(javaPackage);
		return project;
	}

}
