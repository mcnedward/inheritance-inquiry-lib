package com.mcnedward.ii.service;

import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.IMethodBinding;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.element.method.JavaMethodInvocation;
import com.mcnedward.ii.service.graph.element.HierarchyTree;
import com.mcnedward.ii.service.graph.element.InheritanceTree;
import com.mcnedward.ii.service.graph.element.SolutionMethod;
import com.mcnedward.ii.service.metric.element.DitMetric;
import com.mcnedward.ii.service.metric.element.NocMetric;
import com.mcnedward.ii.service.metric.element.WmcMetric;
import com.mcnedward.ii.utils.IILogger;

/**
 * A service tool for analyzing the Chidamber & Kemerer metrics for a {@link JavaProject}.
 * 
 * @author Edward - Jun 22, 2016
 *
 */
public class AnalyzerService {

	public JavaSolution analyze(JavaProject project) {
		JavaSolution solution = initSolution(project);

		calculateMetricsAndTrees(project, solution, true);
		calculateMethods(project, solution);

		return solution;
	}

	public JavaSolution analyzeMetrics(JavaProject project) {
		JavaSolution solution = initSolution(project);
		calculateMetricsAndTrees(project, solution, true);
		return solution;
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
	public void calculateMetricsAndTrees(JavaProject project, JavaSolution solution, boolean ignoreZero) {
		for (JavaElement element : project.getAllElements()) {
			int dit = calculateDepthOfInheritanceTree(element, project, solution, ignoreZero);
			calculateNumberOfChildren(element, project, solution, ignoreZero);
			calculateWeightedMethodsPerClass(element, project, solution, ignoreZero);
			calculateInheritanceTrees(element, project, solution, dit);
			calculateHierarchyTrees(element, project, solution);
		}
	}

	/**
	 * Calculates the Depth of Inheritance Tree for a {@link JavaElement} in the {@link JavaProject}.
	 * 
	 * @param element
	 *            The JavaElement
	 * @param project
	 *            The JavaProject
	 * @param solution
	 *            The {@JavaSolution} to place the metric into
	 * @param ignoreZero
	 *            If this is true, then the Analyzer will ignore metrics whose value is zero.
	 * @return int The Depth of Inheritance Tree
	 */
	private int calculateDepthOfInheritanceTree(JavaElement element, JavaProject project, JavaSolution solution, boolean ignoreZero) {
		Stack<JavaElement> classStack = project.findDepthOfInheritanceTreeFor(element);
		int dit = classStack.size();
		int numberOfInheritedMethods = project.findNumberOfInheritedMethodsFor(element);
		if (ignoreZero) {
			if (dit > 0) {
				solution.addDitMetric(new DitMetric(element, dit, numberOfInheritedMethods));
			}
		} else {
			solution.addDitMetric(new DitMetric(element, dit, numberOfInheritedMethods));
		}
		return dit;
	}

	/**
	 * Calculates the Number of Children for all {@link JavaElement}s in the {@link JavaProject}.
	 * 
	 * @param element
	 *            The JavaElement
	 * @param project
	 *            The JavaProject
	 * @param solution
	 *            The {@JavaSolution} to place the metric into
	 * @param ignoreZero
	 *            If this is true, then the Analyzer will ignore metrics whose value is zero.
	 * @return int The Number of Children
	 */
	private int calculateNumberOfChildren(JavaElement element, JavaProject project, JavaSolution solution, boolean ignoreZero) {
		List<JavaElement> classChildren = project.findNumberOfChildrenFor(element);
		int noc = classChildren.size();
		if (ignoreZero) {
			if (noc > 0) {
				solution.addNocMetric(new NocMetric(element, noc, classChildren));
			}
		} else {
			solution.addNocMetric(new NocMetric(element, noc, classChildren));
		}
		return noc;
	}

	/**
	 * Calculates the Weighted Methods per Class for all {@link JavaElement}s in the {@link JavaProject}.
	 * 
	 * @param element
	 *            The JavaElement
	 * @param project
	 *            The JavaProject
	 * @param solution
	 *            The {@JavaSolution} to place the metric into
	 * @param ignoreZero
	 *            If this is true, then the Analyzer will ignore metrics whose value is zero.
	 * @return int The Weighted Methods per Class
	 */
	private int calculateWeightedMethodsPerClass(JavaElement element, JavaProject project, JavaSolution solution, boolean ignoreZero) {
		int wmc = element.getMethods().size();
		if (ignoreZero) {
			if (wmc > 0) {
				solution.addWmcMetric(new WmcMetric(element, wmc));
			}
		} else {
			solution.addWmcMetric(new WmcMetric(element, wmc));
		}
		return wmc;
	}

	/**
	 * Calculates all the child methods of the {@link JavaElement}s in the {@link JavaProject} to see if they override
	 * any of their parent methods.
	 * 
	 * @param project
	 *            The {@link JavaProject}.
	 * @return The {@link SolutionMethod} list
	 */
	public void calculateMethods(JavaProject project, JavaSolution solution) {
		for (JavaElement child : project.getClasses()) {
			if (child.getSuperClasses().isEmpty())
				continue;

			JavaElement parent = child.getSuperClasses().get(0);
			for (JavaMethod childMethod : child.getMethods()) {
				IMethodBinding childBinding = childMethod.getMethodBinding();

				for (JavaMethod parentMethod : parent.getMethods()) {
					IMethodBinding parentBinding = parentMethod.getMethodBinding();
					if (childBinding.overrides(parentBinding)) {
						// Add override method to solution
						solution.addOMethod(new SolutionMethod(childMethod.getName(), childMethod.getSignature(), parent.getName(), child.getName()));
						IILogger.analysis("Method %s in element %s is overriding method %s defined in parent class %s.", childMethod.getSignature(),
								child, parentMethod.getSignature(), parent);

						// Child method overrides parent method, so check to see if the child method contains the parent
						// method's MethodInvocation
						for (JavaMethodInvocation invocation : childMethod.getMethodInvocations()) {
							// The method declaration binding will be the same binding as the one where the method is
							// defined (declared)
							IMethodBinding methodDeclaration = invocation.getMethodBinding().getMethodDeclaration();
							if (methodDeclaration == parentBinding) {
								solution.addEMethod(
										new SolutionMethod(childMethod.getName(), childMethod.getSignature(), parent.getName(), child.getName()));
								IILogger.analysis("Method %s in element %s is extending method %s defined in parent class %s.",
										childMethod.getSignature(), child, parentMethod.getSignature(), parent);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Calculates the hierarchy trees for a {@link JavaElement} in the {@link JavaProject}.
	 * <p>
	 * Currently, this only builds for classes, and only if the DIT is greater than 0.
	 * </p>
	 * 
	 * @param element
	 *            The JavaElement
	 * @param project
	 *            The JavaProject
	 * @param int
	 *            The Depth of Inheritance Tree
	 * @param solution
	 *            The {@link JavaSolution}
	 */
	public void calculateInheritanceTrees(JavaElement element, JavaProject project, JavaSolution solution, int dit) {
		if (!element.isInterface()) {
			if (dit > 1)
				solution.addInheritanceTree(new InheritanceTree(element));
		}
	}

	public void calculateHierarchyTrees(JavaElement element, JavaProject project, JavaSolution solution) {
		HierarchyTree tree = new HierarchyTree(project, element);
		if (tree.hasChildren)
			solution.addHeirarchyTree(tree);
	}

	private JavaSolution initSolution(JavaProject project) {
		return new JavaSolution(project.getName(), project.getSystemName(), project.getVersion());
	}

}
