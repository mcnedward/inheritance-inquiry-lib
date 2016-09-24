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
public class FullHierarchy extends Hierarchy {

	private static final Integer NDC_KEY = -1;
    private  boolean mHasChildren;
    private Collection<FullHierarchy> mExts;
    private Collection<FullHierarchy> mImpls;
    private int mMaxWidth;
    private int mMaxDepth;
    private int mNdc;
	private Map<Integer, Integer> mChildWidthMap;
	private int mStackLevel;
	
	/**
	 * This is the standard constructor used to create a hierarchy.
	 * @param project
	 * @param element
	 */
	public FullHierarchy(JavaProject project, JavaElement element) {
		super(element);
        mExts = new ArrayList<>();
        mImpls = new ArrayList<>();
		// Setup the stack for subclasses
		mChildWidthMap = new ConcurrentHashMap<>();
		mStackLevel = 1;
		mChildWidthMap.put(NDC_KEY, 0);	// TODO: Move Number of Descendants somewhere better
		
		List<JavaElement> projectElements = project.getAllElements();
		buildTree(element, projectElements);
		
		Integer ndc = mExts.size();
		mChildWidthMap.put(mStackLevel, ndc);	// Add the root's children
		mHasChildren = ndc > 0;	// Has children if other classes extended it
		
		ndc = mChildWidthMap.get(NDC_KEY);
		mMaxWidth = 0;
		mMaxDepth = 0;
		for (Map.Entry<Integer, Integer> childCount : mChildWidthMap.entrySet()) {
			// NDC_KEY is for total amount of children, so skip that
			int depth = childCount.getKey();
			if (depth == NDC_KEY) continue;
			Integer width = childCount.getValue();
			if (width > mMaxWidth)
				mMaxWidth = width;
			if (depth > mMaxDepth)
				mMaxDepth = depth;
		}
	}
	
	private FullHierarchy(Collection<JavaElement> projectElements, JavaElement element, Map<Integer, Integer> childCountMap, int parentStackLevel) {
		super(element);
        mExts = new ArrayList<>();
        mImpls = new ArrayList<>();
		// Setup the stack stuff from the parent
		this.mChildWidthMap = childCountMap;
		mStackLevel = parentStackLevel + 1;
		
		buildTree(element, projectElements);
		
		// Add the width of this subclass to the stack
		int width = mExts.size();
		Integer ndc = childCountMap.get(NDC_KEY);
		ndc += width;
		childCountMap.put(NDC_KEY, ndc);
		
		Integer childCount = childCountMap.get(mStackLevel);
		if (childCount == null) {
			childCount = width;
		}
		else {
			childCount += width;
		}
		childCountMap.put(mStackLevel, childCount);
	}

	private void buildTree(JavaElement element, Collection<JavaElement> projectElements) {
		findSubClasses(element, projectElements);
		findInterfaceChildren(element, projectElements);
	}
	
	private void findSubClasses(JavaElement element, Collection<JavaElement> projectElements) {
		for (JavaElement projectElement : projectElements) {
			if (projectElement.getSuperClasses().contains(element)) {
                mExts.add(new FullHierarchy(projectElements, projectElement, mChildWidthMap, mStackLevel));
			}
		}
	}
	
	private void findInterfaceChildren(JavaElement element, Collection<JavaElement> projectElements) {
		for (JavaElement projectElement : projectElements) {
			if (projectElement.getInterfaces().contains(element)) {
				FullHierarchy full = new FullHierarchy(projectElements, projectElement, mChildWidthMap, mStackLevel);
				if (isInterface) {
					if (projectElement.isInterface()) {
                        mExts.add(full);
					} else {
						mImpls.add(full);
					}
				} else {
					// Otherwise, this element is a class and is implementing the projectElement
					mImpls.add(new FullHierarchy(projectElements, projectElement, mChildWidthMap, mStackLevel));
				}
			}
		}
	}

	public Collection<FullHierarchy> getExts() {
        return mExts;
    }

    public Collection<FullHierarchy> getImpls() {
        return mImpls;
    }

    public int getNdc() {
        return mNdc;
    }

    public int getMaxWidth() {
        return mMaxWidth;
    }

    public boolean hasChildren() {
        return mHasChildren;
    }

	@Override
	public String toString() {
		return elementName + " - sub[" + mExts.size() + "] - impl[" + mImpls.size() + "]";
	}

}
