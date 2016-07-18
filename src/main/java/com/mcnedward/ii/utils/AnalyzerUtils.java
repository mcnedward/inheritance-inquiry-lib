package com.mcnedward.ii.utils;

import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.IMethodBinding;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.element.method.JavaMethodInvocation;

/**
 * TODO This should either be deleted, or replaced with AnalyzerService methods
 * @author Edward - Jul 14, 2016
 *
 */
public final class AnalyzerUtils {
	
	public static void analyze(JavaProject project) {
		calculateDepthOfInheritanceTree(project);
		calculateNumberOfChildren(project);
		calculateWeightedMethodsPerClass(project);
		calculateClassesWithHighNOCAndWMC(project);
		calculateOverridenMethods(project);
		calculateExtendedMethods(project);
	}

	/**
	 * Check all the child methods of the {@link JavaElement}s in the {@link JavaProject} to see if they override any of
	 * their parent methods.
	 * 
	 * @param project
	 *            The {@link JavaProject}.
	 */
	public static void calculateOverridenMethods(JavaProject project) {
		IILogger.analysis("********** OVERRIDEN METHODS **********");
		for (JavaElement child : project.getClasses()) {
			if (child.getSuperClasses().isEmpty())
				continue;

			JavaElement parent = child.getSuperClasses().get(0);
			for (JavaMethod childMethod : child.getMethods()) {
				IMethodBinding childBinding = childMethod.getMethodBinding();

				for (JavaMethod parentMethod : parent.getMethods()) {
					IMethodBinding parentBinding = parentMethod.getMethodBinding();
					if (childBinding.overrides(parentBinding)) {
						IILogger.analysis("Method %s in element %s is overriding method %s defined in parent class %s.", childMethod.getSignature(),
								child, parentMethod.getSignature(), parent);
					}
				}
			}
		}
		IILogger.analysis("\n\n");
	}

	/**
	 * Checks all the child methods of the {@link JavaElement}s in the {@link JavaProject} to see if they extend any of
	 * their parent methods.
	 * <p>
	 * Extending a parent method is when a method in a child class overrides a superclass method to provide additional
	 * functionality, while still invoking the superclass method.
	 * </p>
	 * <p>
	 * Superclass:<br>
	 * 
	 * <pre>
	 * <code>public void save(T entity) {
	 *     persist(entity);
	 * }
	 * </pre>
	 * </p>
	 * 
	 * <p>
	 * Child class:
	 * 
	 * <pre>
	 * <code>public void save(Account account) {
	 *     validate(account);
	 *     super.save(account)
	 * }
	 * </pre>
	 * </p>
	 * 
	 * @param project
	 *            The {@link JavaProject}
	 */
	public static void calculateExtendedMethods(JavaProject project) {
		IILogger.analysis("********** EXTENDED METHODS **********");
		for (JavaElement child : project.getClasses()) {
			if (child.getSuperClasses().isEmpty())
				continue;

			JavaElement parent = child.getSuperClasses().get(0);
			for (JavaMethod childMethod : child.getMethods()) {
				IMethodBinding childBinding = childMethod.getMethodBinding();
				
				for (JavaMethod parentMethod : parent.getMethods()) {
					IMethodBinding parentBinding = parentMethod.getMethodBinding();
					if (childBinding.overrides(parentBinding)) {
						// Child method overrides parent method, so check to see if the child method contains the parent
						// method's MethodInvocation
						for (JavaMethodInvocation invocation : childMethod.getMethodInvocations()) {
							// The method declaration binding will be the same binding as the one where the method is
							// defined (declared)
							IMethodBinding methodDeclaration = invocation.getMethodBinding().getMethodDeclaration();
							if (methodDeclaration == parentBinding) {
								IILogger.analysis("Method %s in element %s is extending method %s defined in parent class %s.",
										childMethod.getSignature(), child, parentMethod.getSignature(), parent);
							}
						}
					}
				}
			}
		}
		IILogger.analysis("\n\n");
	}

	public static void calculateNumberOfChildren(JavaProject project) {
		IILogger.analysis("********** NUMBER OF CHILDREN **********");
		for (JavaElement element : project.getAllElements()) {
			List<JavaElement> classChildren = project.findNumberOfChildrenFor(element);

			int NOC = classChildren.size();
			if (NOC > 0) {
				IILogger.analysis("Number of children for %s is %s - %s", element, classChildren.size(), classChildren);
			}
		}
		IILogger.analysis("\n\n");
	}

	public static void calculateDepthOfInheritanceTree(JavaProject project) {
		IILogger.analysis("********** DEPTH OF INHERITANCE TREE **********");
		List<JavaElement> projectElements = project.getAllElements();
		for (JavaElement element : projectElements) {
			Stack<JavaElement> classStack = project.findDepthOfInheritanceTreeFor(element);
			int dit = project.findNumberOfInheritedMethodsFor(element);
			if (dit > 0) {
				IILogger.analysis("Depth of inheritance for %s is %s - %s\nNumber of inherited methods: %s", element, classStack.size(), classStack, dit);
			}
		}
		IILogger.analysis("\n\n");
	}

	public static void calculateWeightedMethodsPerClass(JavaProject project) {
		IILogger.analysis("********** WEIGHTED METHODS PER CLASS **********");
		for (JavaElement element : project.getAllElements()) {
			IILogger.analysis("Weighted methods for %s is %s", element, element.getMethods().size());
		}
		IILogger.analysis("\n\n");
	}

	public static void calculateClassesWithHighNOCAndWMC(JavaProject project) {
		IILogger.analysis("********** CLASSES WITH HIGH NOC & WMC **********");
		for (JavaElement element : project.getAllElements()) {
			int total = project.findNOCAndWMCFor(element);
			if (total > 30)
				IILogger.analysis(
						"%s has a high NOC and WMC [%s]. Considering a refactor to separate to reduce the number of methods inherited to children.",
						element, total);
		}
		IILogger.analysis("\n\n");
	}

}
