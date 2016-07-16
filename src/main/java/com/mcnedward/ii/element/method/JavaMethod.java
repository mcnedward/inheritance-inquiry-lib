package com.mcnedward.ii.element.method;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;

import com.mcnedward.ii.element.BaseObject;

/**
 * @author Edward - Jun 24, 2016
 *
 */
public class JavaMethod extends BaseObject {

	private String mReturnType;
	private List<MethodParameter> mMethodParameters;
	private List<MethodCallObject> mMethodCallObjects;	// TODO This may not be needed for this project
	private List<JavaMethodInvocation> mMethodInvocations;
	private String mSignature;
	// For use in method override and extending analysis
	private IMethodBinding mMethodBinding;
	
	
	public JavaMethod() {
		super();
		mMethodParameters = new ArrayList<>();
		mMethodCallObjects = new ArrayList<>();
		mMethodInvocations = new ArrayList<>();
	}
	
	public void addParameter(MethodParameter methodParameter) {
		mMethodParameters.add(methodParameter);
	}
	
	public void addMethodCallObject(MethodCallObject methodCallObject) {
		mMethodCallObjects.add(methodCallObject);
	}
	
	public void addMethodInvocation(JavaMethodInvocation methodInvocation) {
		mMethodInvocations.add(methodInvocation);
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
	
	public List<JavaMethodInvocation> getMethodInvocations() {
		return mMethodInvocations;
	}

	/**
	 * @param mMethodCallObjects the mMethodCallObjects to set
	 */
	public void setMethodCallObjects(List<MethodCallObject> methodCallObjects) {
		mMethodCallObjects = methodCallObjects;
	}
	
	public String getSignature() {
		return mSignature;
	}
	
	public void setSignature(String signature) {
		mSignature = signature;
	}
	
	public IMethodBinding getMethodBinding() {
		return mMethodBinding;
	}
	
	public void setMethodBinding(IMethodBinding binding) {
		mMethodBinding = binding;
	}
	
	@Override
	public String toString() {
		return mSignature;
	}
	
}
