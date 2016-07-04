package com.mcnedward.ii.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward - Jul 1, 2016
 *
 */
public class MethodHolder {
	
	private String mName;
	private List<String> mTypeArgs;
	
	public MethodHolder() {
		mTypeArgs = new ArrayList<>();
	}

	public MethodHolder(String name) {
		this();
		mName= name;
	}
	
	public void addTypeArg(String name) {
		mTypeArgs.add(name);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public List<String> getTypeArgs() {
		return mTypeArgs;
	}

	public void setTypeArgs(List<String> typeArgs) {
		mTypeArgs = typeArgs;
	}

}
