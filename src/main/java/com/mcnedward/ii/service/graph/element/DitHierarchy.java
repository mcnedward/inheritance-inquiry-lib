package com.mcnedward.ii.service.graph.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.JavaElement;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class DitHierarchy extends Hierarchy {

	private int mDit;
    private Stack<List<DitHierarchy>> mTree;
    private List<DitHierarchy> mAncestors;
    private int mInheritedMethodCount;
    private int mElementMethodCount;

	public DitHierarchy(JavaElement element) {
        super(element);
        mTree = new Stack<>();
        mAncestors = new ArrayList<>();
		// Get all the methods for the element that this metric is for
		mElementMethodCount = element.getWeightedMethodCount();
		buildTree(element);
	}

	private DitHierarchy(JavaElement element, boolean isParent) {
        super(element);
        mTree = new Stack<>();
        mAncestors = new ArrayList<>();
		mElementMethodCount = element.getInheritableMethodCount();
		buildTree(element);
	}
	
	private void buildTree(JavaElement element) {
		List<DitHierarchy> holder = travelHierarchies(element);
		mAncestors = holder;
		// +1 for classes since they inherit from java.lang.Object 
		mDit = mTree.size() + (isInterface ? 0 : 1);
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
				parentHierarchy.mTree.add(parentHierarchies);
				
				// Add to the inherited method count
				mInheritedMethodCount += parentHierarchy.mElementMethodCount;
			}
			mTree.add(hierarchies);
		}
		return hierarchies;
	}

    public int getDit() {
        return mDit;
    }

    public List<DitHierarchy> getAncestors() {
        return mAncestors;
    }

    public int getInheritedMethodCount() {
        return mInheritedMethodCount;
    }

    public int getElementMethodCount() {
        return mElementMethodCount;
    }

    public Stack<List<DitHierarchy>> getTree() {
        return mTree;
    }

	@Override
	public String toString() {
		return elementName + " DIT[" + mDit + "]";
	}

}
