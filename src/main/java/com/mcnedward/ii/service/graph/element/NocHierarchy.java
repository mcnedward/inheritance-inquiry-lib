package com.mcnedward.ii.service.graph.element;

import java.util.List;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class NocHierarchy extends Hierarchy<NocHierarchy> {

	public int noc; 
	public boolean hasChildren;

	public NocHierarchy(JavaProject project, JavaElement element) {
		super(project, element);
	}

	@Override
	protected void buildTree(JavaProject project, JavaElement element) {
		List<JavaElement> children = project.findNumberOfChildrenFor(element);
		noc = children.size();
		inheritedMethodCount = element.getWeightedMethodCount();
		hasChildren = children.size() > 0;
		if (hasChildren) {
			for (JavaElement child : children) {
				hierarchyTrees.add(new NocHierarchy(project, child));
			}
		}
	}

}
