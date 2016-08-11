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
	public Collection<FullHierarchy> exts;
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
		exts = new ArrayList<>();
		impls = new ArrayList<>();
	}
	
	protected void buildTree(JavaElement element, Collection<JavaElement> projectElements) {
		findSubClasses(element, projectElements);
		findInterfaceChildren(element, projectElements);
	}
	
	private void findSubClasses(JavaElement element, Collection<JavaElement> projectElements) {
		for (JavaElement projectElement : projectElements) {
			if (projectElement.getSuperClasses().contains(element)) {
				exts.add(new FullHierarchy(projectElements, projectElement));
			}
		}
	}
	
	private void findInterfaceChildren(JavaElement element, Collection<JavaElement> projectElements) {
		for (JavaElement projectElement : projectElements) {
			if (projectElement.getInterfaces().contains(element)) {
				FullHierarchy full = new FullHierarchy(projectElements, projectElement);	
				if (isInterface) {
					if (projectElement.isInterface()) {
						exts.add(full);
					} else {
						impls.add(full);
					}
				} else {
					// Otherwise, this element is a class and is implementing the projectElement
					impls.add(new FullHierarchy(projectElements, projectElement));
				}
			}
		}
	}

	@Override
	public String toString() {
		return elementName + " - sub[" + exts.size() + "] - impl[" + impls.size() + "]";
	}
	
}
