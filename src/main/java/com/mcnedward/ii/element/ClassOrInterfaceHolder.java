package com.mcnedward.ii.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward - Jul 1, 2016
 *
 */
public class ClassOrInterfaceHolder {
	
	private String mName;
	private List<String> mTypeArgs;
	private boolean mIsInterface;
	
	public ClassOrInterfaceHolder() {
		mTypeArgs = new ArrayList<>();
	}

	public ClassOrInterfaceHolder(String name, boolean isInterface) {
		this();
		mName= name;
		mIsInterface = isInterface;
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
	
	public boolean isInterface() {
		return mIsInterface;
	}

}
