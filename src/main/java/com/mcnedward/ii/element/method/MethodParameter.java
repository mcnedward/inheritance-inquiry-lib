package com.mcnedward.ii.element.method;

/**
 * @author Edward - Jun 24, 2016
 *
 */
public class MethodParameter {
	private String parameterType;
	private String parameterName;

	public MethodParameter() {

	}

	public MethodParameter(String parameterType, String parameterName) {
		this.parameterType = parameterType;
		this.parameterName = parameterName;
	}

	/**
	 * @return the parameterType
	 */
	public String getParameterType() {
		return parameterType;
	}

	/**
	 * @param parameterType
	 *            the parameterType to set
	 */
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * @param parameterName
	 *            the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	@Override
	public String toString() {
		return parameterType + " " + parameterName;
	}
}
