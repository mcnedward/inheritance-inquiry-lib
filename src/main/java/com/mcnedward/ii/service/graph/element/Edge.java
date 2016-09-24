package com.mcnedward.ii.service.graph.element;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public class Edge implements IGraphItem {
	
	private static int EDGE_ID = 1;
	
	private Node mFrom;
	private Node mTo;
	private int mId;
	private String mName;
	private boolean mIsImplements;
	
	public Edge(String name, Node to, Node from) {
		this(name, to, from, false);
	}
	
	public Edge(String name, Node to, Node from, boolean isImplements) {
		mId = EDGE_ID++;
		mName = name;
		mTo = to;
		mFrom = from;
		mIsImplements = isImplements;
	}
	
	public Node from() {
		return mFrom;
	}
	
	public Node to() {
		return mTo;
	}
	
	public boolean isImplements() {
		return mIsImplements;
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
