package com.mcnedward.ii.service.graph.element;

import java.util.List;
import java.util.Stack;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class NocHierarchy extends Hierarchy {

	private static final int NOC_LIMIT = 10;
	private static final int WMC_LIMIT = 21;
	
	private int mNoc;
    private int mInheritedMethodCount;
    private Stack<NocHierarchy> mTree;
    private boolean mHasChildren;
    private boolean mIsOverNocAndWmcLimit;
    private int mWmc;
	
	/**
	 * This is the standard constructor used to create a hierarchy.
	 * @param project
	 * @param element
	 */
	public NocHierarchy(JavaProject project, JavaElement element) {
        super(element);
        mTree = new Stack<>();
        mWmc = element.getWeightedMethodCount();
		buildTree(project, element);
		mIsOverNocAndWmcLimit = mNoc > NOC_LIMIT && mWmc > WMC_LIMIT;
	}

	/**
	 * Used to create the children
	 * @param project
	 * @param element
	 * @param parentMethodCount
	 */
	private NocHierarchy(JavaProject project, JavaElement element, int parentMethodCount) {
        super(element);
        mTree = new Stack<>();
        mWmc = element.getWeightedMethodCount();
		// Number of methods from parent, plus number of methods from this child
		mInheritedMethodCount = parentMethodCount + mWmc;
	}

	private void buildTree(JavaProject project, JavaElement element) {
		List<JavaElement> children = project.findNumberOfChildrenFor(element);
		mNoc = children.size();
		mHasChildren = children.size() > 0;
		if (mHasChildren) {
			for (JavaElement child : children) {
				mTree.add(new NocHierarchy(project, child, mWmc));
			}
		}
	}

    public Integer getNoc() {
        return mNoc;
    }

    public int getInheritedMethodCount() {
        return mInheritedMethodCount;
    }

    public Stack<NocHierarchy> getTree() {
        return mTree;
    }

    public int getWmc() {
        return mWmc;
    }

    public boolean isOverNocAndWmcLimit() {
        return mIsOverNocAndWmcLimit;
    }

    public boolean hasChildren() {
        return mHasChildren;
    }

    @Override
    public String toString() {
        return elementName + " NOC[" + mNoc + "]";
    }

}
