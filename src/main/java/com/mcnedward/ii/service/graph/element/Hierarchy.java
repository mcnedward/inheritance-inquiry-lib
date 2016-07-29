package com.mcnedward.ii.service.graph.element;

import java.util.Stack;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public abstract class Hierarchy<T> {

	public Stack<T> hierarchyTrees;
	public JavaProject project;
	public String element;
	public int inheritedMethodCount;
	
	public Hierarchy(JavaProject project, JavaElement element) {
		this.project = project;
		this.element = element.getName();
		hierarchyTrees = new Stack<>();
		buildTree(project, element);
	}
	
	protected abstract void buildTree(JavaProject project, JavaElement element);

}
