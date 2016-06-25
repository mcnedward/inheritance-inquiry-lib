package com.mcnedward.ii.element;

import java.util.List;

/**
 * @author Edward - Jun 24, 2016
 *
 */
public class MethodCallObject {
	private String name;
	private List<String> arguments;
	private String scope;
	private boolean isThisExpr;
	private int lineNumber;
	private String parentClass;
	
	public MethodCallObject() {
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the arguments
	 */
	public List<String> getArguments() {
		return arguments;
	}


	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}


	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}


	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}


	/**
	 * @return the isThisExpr
	 */
	public boolean isThisExpr() {
		return isThisExpr;
	}


	/**
	 * @param isThisExpr the isThisExpr to set
	 */
	public void setThisExpr(boolean isThisExpr) {
		this.isThisExpr = isThisExpr;
	}


	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}


	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}


	/**
	 * @return the parentClass
	 */
	public String getParentClass() {
		return parentClass;
	}


	/**
	 * @param parentClass the parentClass to set
	 */
	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}


	@Override
	public String toString() {
		String value = "Method Call: [" + lineNumber + "] ";
		if (!"".equals(scope))
			value += scope + ".";
		value += name + "(";
		if (!arguments.isEmpty()) {
			for (int x = 0; x < arguments.size(); x++) {
				value += arguments.get(x);
				if (x != arguments.size() - 1)
					value += ", ";
			}
		}
		value += ")";
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + (isThisExpr ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodCallObject other = (MethodCallObject) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (isThisExpr) {
			String newScope = scope.replaceAll("this.", "");
			if (newScope.equals(other.scope))
				return true;
			else
				return false;
		} else if (other.isThisExpr) {
			String newScope = other.scope.replaceAll("this.",  "");
			if (newScope.equals(scope))
				return true;
			else
				return false;
		}
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
