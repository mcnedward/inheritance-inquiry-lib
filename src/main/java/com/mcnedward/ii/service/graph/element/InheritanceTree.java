package com.mcnedward.ii.service.graph.element;

import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class InheritanceTree {

	public Stack<String> inheritanceTree;
	
	public InheritanceTree(JavaElement element) {
		inheritanceTree = new Stack<>();
		
		// TODO Handle interfaces as well
		
		buildInheritanceTree(element);
	}
	
	private void buildInheritanceTree(JavaElement element) {
		// Add the element to the tree
		inheritanceTree.add(element.getName());
		
		// Search for any more super classes
		List<ClassOrInterfaceElement> superClasses = element.getSuperClassCois();
		if (superClasses.isEmpty()) return;
		
		ClassOrInterfaceElement coiElement = superClasses.get(0);
		buildInheritanceTree(coiElement.getElement());
	}
	
}
