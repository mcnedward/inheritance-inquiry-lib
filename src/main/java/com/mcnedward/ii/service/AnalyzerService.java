package com.mcnedward.ii.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.IMethodBinding;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.element.method.JavaMethodInvocation;
import com.mcnedward.ii.service.graph.SolutionMethod;
import com.mcnedward.ii.service.metric.DitMetric;
import com.mcnedward.ii.service.metric.NocMetric;
import com.mcnedward.ii.service.metric.WmcMetric;
import com.mcnedward.ii.utils.IILogger;

/**
 * A service tool for analyzing the Chidamber & Kemerer metrics for a {@link JavaProject}.
 * 
 * @author Edward - Jun 22, 2016
 *
 */
public class AnalyzerService {

	public JavaSolution analyze(JavaProject project) {
		return new JavaSolution(project.getName(), project.getSystemName(), project.getVersion(), calculateDepthOfInheritanceTree(project),
				calculateNumberOfChildren(project), calculateWeightedMethodsPerClass(project), calculateOverriddenMethods(project),
				calculateExtendedMethods(project));
	}

	/**
	 * Calculates the Depth of Inheritance Tree for all {@link JavaElement}s in the {@link JavaProject}.
	 * 
	 * @param project
	 *            The JavaProject
	 * @param ignoreZero
	 *            If this is true, then the Analyzer will ignore metrics whose value is zero.
	 * @return
	 */
	public List<DitMetric> calculateDepthOfInheritanceTree(JavaProject project, boolean ignoreZero) {
		List<DitMetric> ditList = new ArrayList<>();
		List<JavaElement> projectElements = project.getAllElements();
		for (JavaElement element : projectElements) {
			Stack<JavaElement> classStack = project.findDepthOfInheritanceTreeFor(element);
			int dit = classStack.size();
			int numberOfInheritedMethods = project.findNumberOfInheritedMethodsFor(element);

			if (ignoreZero) {
				if (dit > 0) {
					ditList.add(new DitMetric(element, dit, numberOfInheritedMethods));
				}
			} else {
				ditList.add(new DitMetric(element, dit, numberOfInheritedMethods));
			}
		}
		return ditList;
	}

	/**
	 * Calculates the Depth of Inheritance Tree for all {@link JavaElement}s in the {@link JavaProject}. This ignores
	 * metrics whose value is 0.
	 * 
	 * @param project
	 *            The JavaProject
	 * @return
	 */
	public List<DitMetric> calculateDepthOfInheritanceTree(JavaProject project) {
		return calculateDepthOfInheritanceTree(project, true);
	}

	/**
	 * Calculates the Number of Children for all {@link JavaElement}s in the {@link JavaProject}.
	 * 
	 * @param project
	 *            The JavaProject
	 * @param ignoreZero
	 *            If this is true, then the Analyzer will ignore metrics whose value is zero.
	 * @return
	 */
	public List<NocMetric> calculateNumberOfChildren(JavaProject project, boolean ignoreZero) {
		List<NocMetric> nocList = new ArrayList<>();
		for (JavaElement element : project.getAllElements()) {
			List<JavaElement> classChildren = project.findNumberOfChildrenFor(element);
			int noc = classChildren.size();

			if (ignoreZero) {
				if (noc > 0) {
					nocList.add(new NocMetric(element, noc, classChildren));
				}
			} else {
				nocList.add(new NocMetric(element, noc, classChildren));
			}
		}
		return nocList;
	}

	/**
	 * Calculates the Number of Children for all {@link JavaElement}s in the {@link JavaProject}. This ignores metrics
	 * whose value is 0.
	 * 
	 * @param project
	 *            The JavaProject
	 * @return
	 */
	public List<NocMetric> calculateNumberOfChildren(JavaProject project) {
		return calculateNumberOfChildren(project, true);
	}

	/**
	 * Calculates the Weighted Methods per Class for all {@link JavaElement}s in the {@link JavaProject}.
	 * 
	 * @param project
	 *            The JavaProject
	 * @param ignoreZero
	 *            If this is true, then the Analyzer will ignore metrics whose value is zero.
	 * @return
	 */
	public List<WmcMetric> calculateWeightedMethodsPerClass(JavaProject project, boolean ignoreZero) {
		List<WmcMetric> wmcList = new ArrayList<>();
		for (JavaElement element : project.getAllElements()) {
			int wmc = element.getMethods().size();
			if (ignoreZero) {
				if (wmc > 0) {
					wmcList.add(new WmcMetric(element, wmc));
				}
			} else {
				wmcList.add(new WmcMetric(element, wmc));
			}
		}
		return wmcList;
	}

	/**
	 * Calculates the Weighted Methods per Class for all {@link JavaElement}s in the {@link JavaProject}. This ignores
	 * metrics whose value is 0.
	 * 
	 * @param project
	 *            The JavaProject
	 * @return
	 */
	public List<WmcMetric> calculateWeightedMethodsPerClass(JavaProject project) {
		return calculateWeightedMethodsPerClass(project, true);
	}

	/**
	 * Check all the child methods of the {@link JavaElement}s in the {@link JavaProject} to see if they override any of
	 * their parent methods.
	 * 
	 * @param project
	 *            The {@link JavaProject}.
	 * @return The {@link SolutionMethod} list
	 */
	public List<SolutionMethod> calculateOverriddenMethods(JavaProject project) {
		List<SolutionMethod> overriddenMethods = new ArrayList<>();
		for (JavaElement child : project.getClasses()) {
			if (child.getSuperClasses().isEmpty())
				continue;

			JavaElement parent = child.getSuperClasses().get(0);
			for (JavaMethod childMethod : child.getMethods()) {
				IMethodBinding childBinding = childMethod.getMethodBinding();

				for (JavaMethod parentMethod : parent.getMethods()) {
					IMethodBinding parentBinding = parentMethod.getMethodBinding();
					if (childBinding.overrides(parentBinding)) {
						overriddenMethods.add(new SolutionMethod(childMethod.getName(), childMethod.getSignature(), parent.getName(), child.getName()));
						IILogger.analysis("Method %s in element %s is overriding method %s defined in parent class %s.", childMethod.getSignature(),
								child, parentMethod.getSignature(), parent);
					}
				}
			}
		}
		return overriddenMethods;
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
	public List<SolutionMethod> calculateExtendedMethods(JavaProject project) {
		List<SolutionMethod> extendedMethods = new ArrayList<>();
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
								extendedMethods
										.add(new SolutionMethod(childMethod.getName(), childMethod.getSignature(), parent.getName(), child.getName()));
								IILogger.analysis("Method %s in element %s is extending method %s defined in parent class %s.",
										childMethod.getSignature(), child, parentMethod.getSignature(), parent);
							}
						}
					}
				}
			}
		}
		return extendedMethods;
	}

}
