package com.mcnedward.ii;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.IMethodBinding;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.element.method.JavaMethodInvocation;

/**
 * @author Edward - Jun 22, 2016
 *
 */
public class Analyzer {
	private static final Logger logger = Logger.getLogger(Analyzer.class);

	public void analyze(JavaProject project) {
		calculateDepthOfInheritance(project);
		calculateNumberOfChildren(project);
		calculateWeightedMethodsPerClass(project);
		findClassesWithHighNOCAndWMC(project);
	}

	public static void calculateDepthOfInheritance(JavaProject project) {
		List<JavaElement> projectElements = project.getAllElements();
		for (JavaElement element : projectElements) {
			Stack<JavaElement> classStack = project.findDepthOfInheritanceTreeFor(element);
			int DOT = project.findNumberOfInheritedMethodsFor(element);
			System.out.println(String.format("Depth of inheritance for %s is %s - %s\nNumber of inherited methods: %s", element, classStack.size(),
					classStack, DOT));
		}
	}

	/**
	 * Check all the child methods of the {@link JavaElement}s in the {@link JavaProject} to see if they override any of
	 * their parent methods.
	 * 
	 * @param project
	 *            The {@link JavaProject}.
	 */
	public static void calculateOverridenMethods(JavaProject project) {
		for (JavaElement child : project.getClasses()) {
			if (child.getSuperClasses().isEmpty())
				continue;

			JavaElement parent = child.getSuperClasses().get(0);
			for (JavaMethod childMethod : child.getMethods()) {
				IMethodBinding childBinding = childMethod.getMethodBinding();

				for (JavaMethod parentMethod : parent.getMethods()) {
					IMethodBinding parentBinding = parentMethod.getMethodBinding();
					if (childBinding.overrides(parentBinding)) {
						logger.info(String.format("Method %s in element %s is overriding method %s defined in parent class %s.",
								childMethod.getSignature(), child, parentMethod.getSignature(), parent));
					}
				}
			}
		}
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
	 * <pre>
	 * <code>public void save(Account account) {
	 *     validate(account);
	 *     super.save(account)
	 * }
	 * </pre>
	 * </p>
	 * @param project The {@link JavaProject}
	 */
	public static void calculateExtendedMethods(JavaProject project) {
		for (JavaElement child : project.getClasses()) {
			if (child.getSuperClasses().isEmpty()) continue;
			
			JavaElement parent = child.getSuperClasses().get(0);
			for (JavaMethod childMethod : child.getMethods()) {
				IMethodBinding childBinding = childMethod.getMethodBinding();

				for (JavaMethod parentMethod : parent.getMethods()) {
					IMethodBinding parentBinding = parentMethod.getMethodBinding();
					if (childBinding.overrides(parentBinding)) {
						// Child method overrides parent method, so check to see if the child method contains the parent method's MethodInvocation
						for (JavaMethodInvocation invocation : childMethod.getMethodInvocations()) {
//							if (invocation.getMethodBinding().)
						}
						
						logger.info(String.format("Method %s in element %s is overriding method %s defined in parent class %s.",
								childMethod.getSignature(), child, parentMethod.getSignature(), parent));
					}
				}
			}
		}
	}

	private void calculateNumberOfChildren(JavaProject project) {
		System.out.println("********** Number of Children **********");
		for (JavaElement element : project.getAllElements()) {
			List<JavaElement> classChildren = project.findNumberOfChildrenFor(element);
			System.out.println(String.format("Number of children for %s is %s - %s", element, classChildren.size(), classChildren));
		}
		System.out.println();
	}

	private void calculateWeightedMethodsPerClass(JavaProject project) {
		System.out.println("********** Weighted Methods Per Class **********");
		for (JavaElement element : project.getAllElements()) {
			System.out.println(String.format("Weighted methods for %s is %s", element, element.getMethods().size()));
		}
		System.out.println();
	}

	private void findClassesWithHighNOCAndWMC(JavaProject project) {
		System.out.println("********** Classes With High NOC & WMC **********");
		for (JavaElement element : project.getAllElements()) {
			int total = project.findNOCAndWMCFor(element);
			if (total > 30)
				System.out.println(String.format(
						"%s has a high NOC and WMC [%s]. Considering a refactor to separate to reduce the number of methods inherited to children.",
						element, total));
		}
		System.out.println();
	}
}
