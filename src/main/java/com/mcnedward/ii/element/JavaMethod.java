package com.mcnedward.ii.element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward - Jun 24, 2016
 *
 */
public class JavaMethod extends BaseObject {

	private String mReturnType;
	private List<MethodParameter> mMethodParameters;
	private List<MethodCallObject> mMethodCallObjects;	// TODO This may not be needed for this project
	
	public JavaMethod() {
		super();
		mMethodParameters = new ArrayList<>();
		mMethodCallObjects = new ArrayList<>();
	}
	
	public void addParameter(MethodParameter methodParameter) {
		mMethodParameters.add(methodParameter);
	}
	
	public void addMethodCallObject(MethodCallObject methodCallObject) {
		mMethodCallObjects.add(methodCallObject);
	}
	
	/**
	 * @return the mReturnType
	 */
	public String getReturnType() {
		return mReturnType;
	}

	/**
	 * @param mReturnType the mReturnType to set
	 */
	public void setReturnType(String returnType) {
		mReturnType = returnType;
	}

	/**
	 * @return the mMethodParameters
	 */
	public List<MethodParameter> getMethodParameters() {
		return mMethodParameters;
	}

	/**
	 * @param mMethodParameters the mMethodParameters to set
	 */
	public void setMethodParameters(List<MethodParameter> methodParameters) {
		mMethodParameters = methodParameters;
	}

	/**
	 * @return the mMethodCallObjects
	 */
	public List<MethodCallObject> getMethodCallObjects() {
		return mMethodCallObjects;
	}

	/**
	 * @param mMethodCallObjects the mMethodCallObjects to set
	 */
	public void setMethodCallObjects(List<MethodCallObject> methodCallObjects) {
		mMethodCallObjects = methodCallObjects;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < mModifiers.size(); i++) {
			Modifier modifier = mModifiers.get(i);
			builder.append(modifier.name + " ");
		}
		builder.append(mReturnType + " " + mName + "(");
		for (int i = 0; i < mMethodParameters.size(); i++) {
			MethodParameter param = mMethodParameters.get(0);
			builder.append(param.toString());
			if (i != mMethodParameters.size() - 1) {
				builder.append(", ");
			}
		}
		builder.append(")");
		return builder.toString();
	}
	
}
