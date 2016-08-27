package com.mcnedward.ii.service.graph.element;

import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class NocHierarchy {

	private static final int NOC_LIMIT = 10;
	private static final int WMC_LIMIT = 21;
	
	public int noc;
	public String elementName;
	public String fullyQualifiedElementName;
	public String path;
	public int inheritedMethodCount;
	public Stack<NocHierarchy> tree;
	public boolean hasChildren;
	public boolean isOverNocAndWmcLimit;
	public int wmc;
	
	/**
	 * This is the standard constructor used to create a hierarchy.
	 * @param project
	 * @param element
	 */
	public NocHierarchy(JavaProject project, JavaElement element) {
		init(element);
		buildTree(project, element);
		isOverNocAndWmcLimit = noc > NOC_LIMIT && wmc > WMC_LIMIT;
	}

	/**
	 * Used to create the children
	 * @param project
	 * @param element
	 * @param parentMethodCount
	 */
	private NocHierarchy(JavaProject project, JavaElement element, int parentMethodCount) {
		init(element);
		// Number of methods from parent, plus number of methods from this child
		inheritedMethodCount = parentMethodCount + wmc;
	}
	
	private void init(JavaElement element) {
		elementName = element.getName();
		fullyQualifiedElementName = element.getFullyQualifiedName();
		tree = new Stack<>();

		path = element.getPackageName().replace(".", "/");
		wmc = element.getWeightedMethodCount();		
	}
	
	private void buildTree(JavaProject project, JavaElement element) {
		List<JavaElement> children = project.findNumberOfChildrenFor(element);
		noc = children.size();
		hasChildren = children.size() > 0;
		if (hasChildren) {
			for (JavaElement child : children) {
				tree.add(new NocHierarchy(project, child, wmc));
			}
		}
	}

	@Override
	public String toString() {
		return fullyQualifiedElementName;
	}
}
