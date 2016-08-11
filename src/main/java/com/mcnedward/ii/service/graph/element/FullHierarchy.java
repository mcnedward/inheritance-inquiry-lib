package com.mcnedward.ii.service.graph.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class FullHierarchy {

	public String elementName;
	public String fullElementName;
	public boolean isInterface;
	public Collection<FullHierarchy> subclasses;
	public Collection<FullHierarchy> impls;
	
	/**
	 * This is the standard constructor used to create a hierarchy.
	 * @param project
	 * @param element
	 */
	public FullHierarchy(JavaProject project, JavaElement element) {
		init(element);
		List<JavaElement> projectElements = project.getAllElements();
		buildTree(element, projectElements);
	}
	
	private FullHierarchy(Collection<JavaElement> projectElements, JavaElement element) {
		init(element);
		buildTree(element, projectElements);
	}

	private void init(JavaElement element) {
		elementName = element.getName();
		fullElementName = element.getFullyQualifiedName();
		isInterface = element.isInterface();
		subclasses = new ArrayList<>();
		impls = new ArrayList<>();
	}
	
	protected void buildTree(JavaElement element, Collection<JavaElement> projectElements) {
		subclasses.addAll(findSubClasses(element, projectElements));
		impls.addAll(findInterfaceChildren(element, projectElements));
	}
	
	private Collection<FullHierarchy> findSubClasses(JavaElement element, Collection<JavaElement> projectElements) {
		List<FullHierarchy> subclasses = new ArrayList<>();
		
		for (JavaElement projectElement : projectElements) {
			if (projectElement.getSuperClasses().contains(element)) {
				subclasses.add(new FullHierarchy(projectElements, projectElement));
			}
		}
		
		return subclasses;
	}
	
	private Collection<FullHierarchy> findInterfaceChildren(JavaElement element, Collection<JavaElement> projectElements) {
		List<FullHierarchy> children = new ArrayList<>();
		
		for (JavaElement projectElement : projectElements) {
			if (projectElement.getInterfaces().contains(element)) {
				children.add(new FullHierarchy(projectElements, projectElement));
			}
		}
		
		return children;
	}

	@Override
	public String toString() {
		return elementName + " - sub[" + subclasses.size() + "] - impl[" + impls.size() + "]";
	}
	
}
