package com.mcnedward.ii.service.graph.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.JavaElement;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class DitHierarchy {

	public int dit;
	public String element;
	public int inheritedMethodCount;
	public Stack<List<DitHierarchy>> tree;
	public List<DitHierarchy> ancestors;
	protected int elementMethodCount;

	public DitHierarchy(JavaElement element) {
		this.element = element.getName();
		elementMethodCount = element.getWeightedMethodCount();
		tree = new Stack<>();
		ancestors = new ArrayList<>();
		buildTree(element);
	}

	private void buildTree(JavaElement element) {
		List<DitHierarchy> holder = travelHierarchies(element);
		ancestors = holder;
		dit = tree.size();
	}

	private List<DitHierarchy> travelHierarchies(JavaElement element) {
		List<JavaElement> parents;
		if (element.isInterface()) {
			parents = element.getInterfaces();
		} else {
			parents = element.getSuperClasses();
		}

		List<DitHierarchy> hierarchies = new ArrayList<>();
		if (!parents.isEmpty()) {
			for (JavaElement parent : parents) {
				DitHierarchy parentHierarchy = new DitHierarchy(parent);
				hierarchies.add(parentHierarchy);
				
				List<DitHierarchy> parentHierarchies = travelHierarchies(parent);
				parentHierarchy.tree.add(parentHierarchies);
			}
			tree.add(hierarchies);
		}
		return hierarchies;
	}

	@Override
	public String toString() {
		return element + " DIT[" + dit + "]";
	}
	
}
