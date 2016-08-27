package com.mcnedward.ii.service.graph.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class FullHierarchy {

	private static final Integer NDC_KEY = -1;
	public String elementName;
	public String fullElementName;
	public boolean isInterface;
	public boolean hasChildren;
	public List<FullHierarchy> exts;
	public Collection<FullHierarchy> impls;
	public int maxWidth;
	public int maxDepth;
	public int ndc;
	private Map<Integer, Integer> childWidthMap;
	private int stackLevel;
	
	/**
	 * This is the standard constructor used to create a hierarchy.
	 * @param project
	 * @param element
	 */
	public FullHierarchy(JavaProject project, JavaElement element) {
		init(element);
		// Setup the stack for subclasses
		childWidthMap = new ConcurrentHashMap<>();
		stackLevel = 1;
		childWidthMap.put(NDC_KEY, 0);	// TODO: Move Number of Descendants somewhere better
		
		List<JavaElement> projectElements = project.getAllElements();
		buildTree(element, projectElements);
		
		Integer ndc = exts.size();
		childWidthMap.put(stackLevel, ndc);	// Add the root's children
		hasChildren = ndc > 0;	// Has children if other classes extended it
		
		ndc = childWidthMap.get(NDC_KEY);
		maxWidth = 0;
		maxDepth = 0;
		for (Map.Entry<Integer, Integer> childCount : childWidthMap.entrySet()) {
			// NDC_KEY is for total amount of children, so skip that
			int depth = childCount.getKey();
			if (depth == NDC_KEY) continue;
			Integer width = childCount.getValue();
			if (width > maxWidth)
				maxWidth = width;
			if (depth > maxDepth)
				maxDepth = depth;
		}
		
		// TODO Can probably remove this, just used for research
//		if (fullElementName.contains("TestCase")) {
//			fullElementName += " ---- " + exts.get(0).fullElementName;
//		}
	}
	
	private FullHierarchy(Collection<JavaElement> projectElements, JavaElement element, Map<Integer, Integer> childCountMap, int parentStackLevel) {
		init(element);
		// Setup the stack stuff from the parent
		this.childWidthMap = childCountMap;
		stackLevel = parentStackLevel + 1;
		
		buildTree(element, projectElements);
		
		// Add the width of this subclass to the stack
		int width = exts.size();
		Integer ndc = childCountMap.get(NDC_KEY);
		ndc += width;
		childCountMap.put(NDC_KEY, ndc);
		
		Integer childCount = childCountMap.get(stackLevel);
		if (childCount == null) {
			childCount = width;
		}
		else {
			childCount += width;
		}
		childCountMap.put(stackLevel, childCount);
	}

	private void init(JavaElement element) {
		elementName = element.getName();
		fullElementName = element.getFullyQualifiedName();
		isInterface = element.isInterface();
		exts = new ArrayList<>();
		impls = new ArrayList<>();
	}
	
	private void buildTree(JavaElement element, Collection<JavaElement> projectElements) {
		findSubClasses(element, projectElements);
		findInterfaceChildren(element, projectElements);
	}
	
	private void findSubClasses(JavaElement element, Collection<JavaElement> projectElements) {
		for (JavaElement projectElement : projectElements) {
			if (projectElement.getSuperClasses().contains(element)) {
				exts.add(new FullHierarchy(projectElements, projectElement, childWidthMap, stackLevel));
			}
		}
	}
	
	private void findInterfaceChildren(JavaElement element, Collection<JavaElement> projectElements) {
		for (JavaElement projectElement : projectElements) {
			if (projectElement.getInterfaces().contains(element)) {
				FullHierarchy full = new FullHierarchy(projectElements, projectElement, childWidthMap, stackLevel);	
				if (isInterface) {
					if (projectElement.isInterface()) {
						exts.add(full);
					} else {
						impls.add(full);
					}
				} else {
					// Otherwise, this element is a class and is implementing the projectElement
					impls.add(new FullHierarchy(projectElements, projectElement, childWidthMap, stackLevel));
				}
			}
		}
	}

	@Override
	public String toString() {
		return elementName + " - sub[" + exts.size() + "] - impl[" + impls.size() + "]";
	}
	
}
