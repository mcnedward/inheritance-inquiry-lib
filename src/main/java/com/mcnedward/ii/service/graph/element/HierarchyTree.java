package com.mcnedward.ii.service.graph.element;

import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class HierarchyTree {

	public String element;
	public int inheritedMethodCount;
	public Stack<HierarchyTree> hierarchyTrees;
	public boolean hasChildren;

	public HierarchyTree(JavaProject project, JavaElement element) {
		hierarchyTrees = new Stack<>();
		this.element = element.getName();
		buildTree(project, element);
	}

	private void buildTree(JavaProject project, JavaElement element) {
		List<JavaElement> children = project.findNumberOfChildrenFor(element);
		inheritedMethodCount = element.getWeightedMethodCount();
		hasChildren = children.size() > 0;
		if (hasChildren) {
			for (JavaElement child : children) {
				hierarchyTrees.add(new HierarchyTree(project, child));
			}
		}
	}

}
