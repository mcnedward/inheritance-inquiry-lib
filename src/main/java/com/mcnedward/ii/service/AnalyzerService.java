package com.mcnedward.ii.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mcnedward.ii.listener.SolutionBuildListener;
import org.eclipse.jdt.core.dom.IMethodBinding;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.element.method.JavaMethod;
import com.mcnedward.ii.element.method.JavaMethodInvocation;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.FullHierarchy;
import com.mcnedward.ii.service.graph.element.NocHierarchy;
import com.mcnedward.ii.service.graph.element.SolutionMethod;
import com.mcnedward.ii.service.metric.MType;
import com.mcnedward.ii.service.metric.element.DitMetric;
import com.mcnedward.ii.service.metric.element.Metric;
import com.mcnedward.ii.service.metric.element.MetricInfo;
import com.mcnedward.ii.service.metric.element.NocMetric;
import com.mcnedward.ii.service.metric.element.WmcMetric;
import com.mcnedward.ii.utils.IILogger;

/**
 * A service tool for analyzing the Chidamber & Kemerer metrics for a {@link JavaProject}.
 * 
 * @author Edward - Jun 22, 2016
 *
 */
public final class AnalyzerService {
	
	public JavaSolution analyze(JavaProject project, SolutionBuildListener listener) {
		JavaSolution solution = initSolution(project);

        IILogger.notify(listener, "Analyzing methods...", 10);
		calculateMethods(project, solution);
        IILogger.notify(listener, "Analyzing metrics and building hierarchies...", 60);
		calculateMetricsAndTrees(project, solution, true);
        IILogger.notify(listener, "Analyzing metric usages...", 100);
		calculateMetricUsages(solution);

		return solution;
	}

	public JavaSolution analyzeMetrics(JavaProject project) {
		JavaSolution solution = initSolution(project);
		calculateMetricsAndTrees(project, solution, true);
		calculateFinalAnalysis(solution);
		return solution;
	}

	public JavaSolution analyzeForDit(JavaProject project) {
		return analyzeForDit(project, null);
	}

	public JavaSolution analyzeForDit(JavaProject project, Integer ditLimit) {
		JavaSolution solution = initSolution(project);
		// Setup the DIT metrics
		for (JavaElement element : project.getAllElements()) {
			if (element.isInterface())
				continue;
			calculateDepthOfInheritanceTree(project, element, solution, true);
		}
		for (DitHierarchy tree : solution.getDitHierarchies()) {
			if (ditLimit != null)
				if (tree.getDit() == ditLimit) {
					solution.addDitHierarchy(tree);
				}
		}
		return solution;
	}

	public JavaSolution analyzeForNoc(JavaProject project) {
		JavaSolution solution = initSolution(project);
		// Setup the DIT metrics
		for (JavaElement element : project.getAllElements()) {
			calculateNumberOfChildren(project, element, solution, true);
		}
		calculateFinalAnalysis(solution);
		return solution;
	}

	public JavaSolution analyzeForFullHierarchy(JavaProject project, String elementToFind) {
		JavaSolution solution = initSolution(project);
		for (JavaElement element : project.getAllElements()) {
			if (elementToFind != null) {
				if (!elementToFind.equals(element.getName()))
					continue;
			}
            calculateFullHierarchyTrees(element, project, solution);
		}
		calculateFinalAnalysis(solution);
		return solution;
	}

	public JavaSolution analyzeForFullHierarchy(JavaProject project) {
		return analyzeForFullHierarchy(project, null);
	}

	/**
	 * Calculates the Depth of Inheritance Tree for all {@link JavaElement}s in the {@link JavaProject}.
	 * 
	 * @param project
	 *            The JavaProject
	 * @param ignoreEmpty
	 *            If this is true, then the Analyzer will ignore metrics whose value should not be included (1 for DIT, 0 for NOC).
	 * @return
	 */
	private void calculateMetricsAndTrees(JavaProject project, JavaSolution solution, boolean ignoreEmpty) {
		for (JavaElement element : project.getAllElements()) {
			calculateDepthOfInheritanceTree(project, element, solution, ignoreEmpty);
			calculateNumberOfChildren(project, element, solution, ignoreEmpty);
			calculateWeightedMethodsPerClass(project, element, solution, ignoreEmpty);
            calculateFullHierarchyTrees(element, project, solution);
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
	 * @param ignoreEmpty
	 *            If this is true, then the Analyzer will ignore metrics whose value is 1.
	 * @return int The Depth of Inheritance Tree
	 */
	private int calculateDepthOfInheritanceTree(JavaProject project, JavaElement element, JavaSolution solution, boolean ignoreEmpty) {
		DitHierarchy hierarchy = new DitHierarchy(element);
		solution.addDitHierarchy(hierarchy);

		int dit = hierarchy.getDit();
		int numberOfInheritedMethods = hierarchy.getInheritedMethodCount();
		if (ignoreEmpty) {
			if (dit > 1) {
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
	 * @param ignoreEmpty
	 *            If this is true, then the Analyzer will ignore metrics whose value is zero.
	 * @return int The Number of Children
	 */
	private int calculateNumberOfChildren(JavaProject project, JavaElement element, JavaSolution solution, boolean ignoreEmpty) {
		List<JavaElement> classChildren = project.findNumberOfChildrenFor(element);
		int noc = classChildren.size();

		NocHierarchy tree = new NocHierarchy(project, element);
		if (tree.hasChildren())
			solution.addNocHeirarchy(tree);
		if (ignoreEmpty) {
			if (noc > 0) {
				solution.addNocMetric(new NocMetric(element, tree.getNoc(), classChildren));
			}
		} else {
			solution.addNocMetric(new NocMetric(element, tree.getNoc(), classChildren));
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
	private int calculateWeightedMethodsPerClass(JavaProject project, JavaElement element, JavaSolution solution, boolean ignoreZero) {
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
	private void calculateMethods(JavaProject project, JavaSolution solution) {
		for (JavaElement child : project.getClasses()) {
			if (child.getSuperClasses().isEmpty())
				continue;

			JavaElement parent = child.getSuperClasses().get(0);
			for (JavaMethod childMethod : child.getMethods()) {
				IMethodBinding childBinding = childMethod.getMethodBinding();
				if (childBinding == null)
					continue;

				for (JavaMethod parentMethod : parent.getMethods()) {
					IMethodBinding parentBinding = parentMethod.getMethodBinding();
					if (parentBinding == null)
						continue;

					if (childBinding.overrides(parentBinding) && !parentMethod.isAbstract()) {
						// Add override method to solution
						solution.addOMethod(new SolutionMethod(childMethod.getName(), childMethod.getSignature(), parent.getName(), child.getName(), child.getFullyQualifiedName()));
						IILogger.analysis("Method %s in element %s is overriding method %s defined in parent class %s.", childMethod.getSignature(),
								child, parentMethod.getSignature(), parent);

						// Child method overrides parent method, so check to see if the child method contains the parent
						// method's MethodInvocation
						for (JavaMethodInvocation invocation : childMethod.getMethodInvocations()) {
							// The method declaration binding will be the same binding as the one where the method is defined (declared)
							IMethodBinding methodDeclaration = invocation.getMethodBinding().getMethodDeclaration();
							if (methodDeclaration == parentBinding) {
								solution.addEMethod(
										new SolutionMethod(childMethod.getName(), childMethod.getSignature(), parent.getName(), child.getName(), child.getFullyQualifiedName()));
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
	 * Used to create the full hierarchy tree structure for an element.
	 * 
	 * @param element
	 * @param project
	 * @param solution
	 */
	private void calculateFullHierarchyTrees(JavaElement element, JavaProject project, JavaSolution solution) {
		FullHierarchy tree = new FullHierarchy(project, element);
		solution.addFullHierarchy(tree);
	}

	private void calculateFinalAnalysis(JavaSolution solution) {
		List<FullHierarchy> fullHierarchies = solution.getFullHierarchies();
		if (!fullHierarchies.isEmpty()) {
			int solutionMaxWidth = 0;
			double averageWidth = 0;
			int ndcMax = 0;
			double ndcAverage = 0;
			int hierarchiesOver0 = 0;
			String maxWidthClass = null;

			for (FullHierarchy h : fullHierarchies) {
				if (h.getMaxWidth() > solutionMaxWidth) {
					solutionMaxWidth = h.getMaxWidth();
					maxWidthClass = h.getFullElementName();
				}
				if (h.hasChildren())
					hierarchiesOver0++;
				averageWidth += h.getMaxWidth();
				
				if (h.getNdc() > ndcMax) {
					ndcMax = h.getNdc();
				}
				ndcAverage += h.getNdc();
			}
			averageWidth = hierarchiesOver0 == 0 ? 0 : averageWidth / hierarchiesOver0;
			ndcAverage = fullHierarchies.size() == 0 ? 0 : ndcAverage / fullHierarchies.size();

			solution.setMaxWidth(solutionMaxWidth);
			solution.setAverageWidth(averageWidth);
			solution.setMaxWidthClass(maxWidthClass);
			solution.setNdcMax(ndcMax);
			solution.setNdcAverage(ndcAverage);
		}
		List<NocHierarchy> nocHierarchies = solution.getNocHierarchies();
		if (!nocHierarchies.isEmpty()) {
			for (NocHierarchy h : nocHierarchies) {
				if (h.isOverNocAndWmcLimit()) {
					solution.addInheritedMethodRisk(h.getFullElementName());
				}
			}
		}
		calculateAverageMethodUsage(solution);
	}
	
	private void calculateAverageMethodUsage(JavaSolution solution) {		
		int oMax = 0, oMin = 0, oTotal = 0, oCount = 0;
		List<String> oMaxClasses = new ArrayList<>();
		for (Map.Entry<String, List<SolutionMethod>> entry : solution.getOMethods().entrySet()) {
			String element = entry.getKey();
			int methodCount = entry.getValue().size();
			
			if (methodCount > oMax) {
				oMax = methodCount;
				oMaxClasses.add(element);
			}
			if (oMin == 0 || methodCount < oMin)
				oMin = methodCount;
			oTotal += methodCount;
			oCount++;
		}
		double oAverage = oCount == 0 ? 0 : (double) (oTotal / oCount);
		solution.setOMethodInfo(new MetricInfo(MType.OM, oMin, oAverage, oMax, oMaxClasses));

		int eMax = 0, eMin = 0, eTotal = 0, eCount = 0;
		List<String> eMaxClasses = new ArrayList<>();
		for (Map.Entry<String, List<SolutionMethod>> entry : solution.getEMethods().entrySet()) {
			String element = entry.getKey();
			int methodCount = entry.getValue().size();
			
			if (methodCount > eMax) {
				eMax = methodCount;
				eMaxClasses.add(element);
			}
			if (eMin == 0 || methodCount < eMin)
				eMin = methodCount;
			eTotal += methodCount;
			eCount++;
		}
		int eAverage = eCount == 0 ? 0 : eTotal / eCount;
		solution.setEMethodInfo(new MetricInfo(MType.EM, eMin, eAverage, eMax, eMaxClasses));
	}

	private void calculateMetricUsages(JavaSolution solution) {
		try {
			solution.setDitMetricInfo(getMetricInfo(solution, MType.DIT));
			solution.setNocMetricInfo(getMetricInfo(solution, MType.NOC));
			solution.setWmcMetricInfo(getMetricInfo(solution, MType.WMC));
		} catch (TaskBuildException e) {
			IILogger.error(e);
		}
	}

	private MetricInfo getMetricInfo(JavaSolution solution, MType metricType) throws TaskBuildException {
		List<? extends Metric> metrics = getMetrics(solution, metricType);
		int min = 0, average = 0, max = 0;
		List<String> maxClasses = new ArrayList<>();
		
		for (int i = 0; i < metrics.size(); i++) {
			int value = metrics.get(i).metric;
			if (i == 0) {
				min = value;
				max = value;
			}
			if (value > max) {
				max = value;
				// TODO This is wrong... this adds max classes that are not the max anymore, I need to filter this at the end of the loop maybe?
				maxClasses.add(metrics.get(i).fullyQualifiedName);
			}
			if (value < min)
				min = value;

			average += value;
		}
		average = (int) Math.ceil(average > 0 ? (double) average / metrics.size() : 0);
		return new MetricInfo(metricType, min, average, max, maxClasses);
	}

	private List<? extends Metric> getMetrics(JavaSolution solution, MType metricType) throws TaskBuildException {
		List<? extends Metric> metrics;
		switch (metricType) {
		case DIT:
			metrics = solution.getDitMetrics();
			break;
		case NOC:
			metrics = solution.getNocMetrics();
			break;
		case WMC:
			metrics = solution.getWmcMetrics();
			break;
		default:
			throw new TaskBuildException("Metric type " + metricType.name() + " is not acceptable for inquiry...");
		}
		return metrics;
	}

	private JavaSolution initSolution(JavaProject project) {
		return new JavaSolution(project);
	}

}
