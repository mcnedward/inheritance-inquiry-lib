package com.mcnedward.ii.service.graph;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public class Node implements IGraphItem {
	
	private static int NODE_ID = 1;
	
	private int mId;
	private String mName;
	
	public Node(String name) {
		mId = NODE_ID++;
		mName = name;
	}
	
	@Override
	public String id() {
		return mName + "-" + mId;
	}
	
	@Override
	public String name() {
		return mName;
	}
	
	@Override
	public String toString() {
		return id(); 
	}

	@Override
	public int hashCode() {
		return mName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Node) {
			return (mName.equals(((Node) obj).name()));
		} else
			return false;
	}
	
}
