package com.mcnedward.ii.service.graph.element;

import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class HierarchyTree {

	public Stack<String> tree;
	
	public HierarchyTree(JavaElement element) {
		tree = new Stack<>();
		
		// TODO Handle interfaces as well
		
		buildInheritanceTree(element);
	}
	
	private void buildInheritanceTree(JavaElement element) {
		// Add the element to the tree
		tree.add(element.getFullyQualifiedName());
		
		// Search for any more super classes
		List<ClassOrInterfaceElement> superClasses = element.getClassOrInterfaceElements();
		if (superClasses.isEmpty()) return;
		
		ClassOrInterfaceElement coiElement = superClasses.get(0);
		buildInheritanceTree(coiElement.getElement());
	}
	
}
