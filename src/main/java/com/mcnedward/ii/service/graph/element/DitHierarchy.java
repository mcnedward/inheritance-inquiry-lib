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
	public String path;
	public boolean isInterface;
	public Stack<List<DitHierarchy>> tree;
	public List<DitHierarchy> ancestors;
	public int inheritedMethodCount;
	public int elementMethodCount;
	
	public DitHierarchy(JavaElement element) {
		init(element);
		// Get all the methods for the element that this metric is for
		elementMethodCount = element.getWeightedMethodCount();
		buildTree(element);
	}

	private DitHierarchy(JavaElement element, boolean isParent) {
		init(element);
		elementMethodCount = element.getInheritableMethodCount();
		buildTree(element);
	}
	
	private void init(JavaElement element) {
		this.element = element.getFullyQualifiedName();
		path = element.getPackageName().replace(".", "/");
		isInterface = element.isInterface();
		tree = new Stack<>();
		ancestors = new ArrayList<>();
	}
	
	private void buildTree(JavaElement element) {
		List<DitHierarchy> holder = travelHierarchies(element);
		ancestors = holder;
		// +1 for classes since they inherit from java.lang.Object 
		dit = tree.size() + (isInterface ? 0 : 1);
	}

	private List<DitHierarchy> travelHierarchies(JavaElement element) {
		List<JavaElement> parents = element.isInterface() ?
				element.getInterfaces() : element.getSuperClasses();

		List<DitHierarchy> hierarchies = new ArrayList<>();
		if (!parents.isEmpty()) {
			for (JavaElement parent : parents) {
				DitHierarchy parentHierarchy = new DitHierarchy(parent, true);
				hierarchies.add(parentHierarchy);
				
				List<DitHierarchy> parentHierarchies = travelHierarchies(parent);
				parentHierarchy.tree.add(parentHierarchies);
				
				// Add to the inherited method count
				inheritedMethodCount += parentHierarchy.elementMethodCount;
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
