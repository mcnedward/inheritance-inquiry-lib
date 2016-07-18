package com.mcnedward.ii.service.graph;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public abstract class Edge implements IGraphItem {
	
	private static int EDGE_ID = 1;
	
	private Node mFrom;
	private Node mTo;
	private int mId;
	private String mName;
	
	public Edge(String name, Node to, Node from) {
		mId = EDGE_ID++;
		mName = name;
		mTo = to;
		mFrom = from;
	}
	
	public Node from() {
		return mFrom;
	}
	
	public Node to() {
		return mTo;
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
		if (obj instanceof Edge) {
			return (mName.equals(((Edge) obj).name()));
		} else
			return false;
	}
	
}
