package com.mcnedward.ii.service.graph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.NocHierarchy;
import com.mcnedward.ii.service.graph.element.SolutionMethod;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public final class GraphService {
	private static final String GRAPH_DIRECTORY_PATH = "C:/users/edward/dev/IIGraphs";
	private static final String HIERARCHY_TREE_DIRECTORY = "hierarchy";
	private static final String IMAGE_TYPE = "png";

	public GraphService() {
	}

	public boolean buildGraphs(JavaSolution solution) {
		try {
			buildOverriddenMethodsGraph(solution);
			buildExtendedMethodsGraph(solution);
			buildDitHierarchyTreeGraph(solution);
			buildNocHierarchyTreeGraphs(solution);
			return true;
		} catch (GraphBuildException e) {
			IILogger.error(e);
			return false;
		}
	}

	public void buildOverriddenMethodsGraph(JavaSolution solution) throws GraphBuildException {
		IILogger.info("Building graph for overridden methods in solution %s...", solution.getSystemName());
		List<SolutionMethod> methods = solution.getOMethods();
		buildMethodsGraph(solution, methods, GType.OMETHODS);
	}

	public void buildExtendedMethodsGraph(JavaSolution solution) throws GraphBuildException {
		IILogger.info("Building graph for extended methods in solution %s...", solution.getSystemName());
		List<SolutionMethod> methods = solution.getEMethods();
		buildMethodsGraph(solution, methods, GType.EMETHODS);
	}

	private void buildMethodsGraph(JavaSolution solution, List<SolutionMethod> methods, GType graphType) throws GraphBuildException {
		IILogger.info("Building graph for solution methods in solution %s...", solution.getSystemName());
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();

		for (SolutionMethod method : methods) {
			Node methodNode = new Node(method.methodSignature);
			Node parentNode = new Node(method.parentElementName);
			Node elementNode = new Node(method.elementName);

			Edge edge1 = new Edge("Parent", methodNode, parentNode);
			Edge edge2 = new Edge("Element", methodNode, elementNode);

			nodes.add(methodNode);
			nodes.add(parentNode);
			nodes.add(elementNode);

			edges.add(edge1);
			edges.add(edge2);
		}

		JungGraph graph = new JungGraph();
		graph.plotGraph(nodes, edges);
		BufferedImage image = graph.createImage();
		writeToFile(solution, graphType, image);
	}

	private void recurseDit(List<DitHierarchy> ancestors, List<Node> nodes, List<Edge> edges, Node parentNode) {
		if (ancestors.isEmpty())
			return;
		for (DitHierarchy ditH : ancestors) {
			Node hierarchyNode = new Node(ditH.element);
			nodes.add(hierarchyNode);

			Edge edge = new Edge("", parentNode, hierarchyNode);
			edges.add(edge);

			recurseDit(ditH.ancestors, nodes, edges, hierarchyNode);
		}
	}

	public boolean buildDitHierarchyTreeGraph(JavaSolution solution) throws GraphBuildException {
		IILogger.info("Building graph for DIT hierarchy tree in solution %s...", solution.getSystemName());
		List<DitHierarchy> trees = solution.getDitHierarchies();
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();

		for (DitHierarchy hierarchy : trees) {
			Node parent = new Node(hierarchy.element);
			nodes.add(parent);
			recurseDit(hierarchy.ancestors, nodes, edges, parent);
		}

		JungGraph graph = new JungGraph(500, 200);
		graph.plotGraph(nodes, edges);
		try {
			BufferedImage image = graph.createImage();
			writeToFile(solution, GType.I_TREE, image);
			return true;
		} catch (OutOfMemoryError e) {
			IILogger.error(String.format("Could not create an inheritance tree graph for %s...", solution.getSystemName()), e);
			return false;
		}
	}

	public void buildNocHierarchyTreeGraphs(JavaSolution solution) throws GraphBuildException {
		IILogger.info("Building graph for NOC hierarchy tree in solution %s...", solution.getSystemName());
		List<NocHierarchy> trees = solution.getNocHierarchies();
		Stack<Node> nodes = new Stack<>();
		Stack<Edge> edges = new Stack<>();

		for (NocHierarchy tree : trees) {
			String parentElement = tree.element;
			Node parentNode = new Node(parentElement);
			nodes.add(parentNode);
			// Create an individual graph for each hierarchy tree
			recurseHierarchyTrees(tree, parentNode, nodes, edges);

			JungGraph graph = new JungGraph(500, 100);
			graph.plotGraph(nodes, edges);
			BufferedImage image = graph.createImage();
			writeToFile(solution, GType.H_TREE, image, HIERARCHY_TREE_DIRECTORY, tree.element);

			nodes = new Stack<>();
			edges = new Stack<>();
		}
	}

	private void recurseHierarchyTrees(NocHierarchy tree, Node parentNode, Stack<Node> nodes, Stack<Edge> edges) {
		Stack<NocHierarchy> hierarchyTree = tree.hierarchyTrees;
		while (!hierarchyTree.isEmpty()) {
			NocHierarchy childTree = hierarchyTree.pop();
			String element = childTree.element;

			Node childNode = new Node(element);
			nodes.add(childNode);
			edges.add(new Edge(String.valueOf(tree.inheritedMethodCount), parentNode, childNode));

			recurseHierarchyTrees(childTree, childNode, nodes, edges);
		}
	}

	private void writeToFile(JavaSolution solution, GType graphType, BufferedImage image, String subDirectory, String name)
			throws GraphBuildException {
		String fileName = getFileName(name, graphType);
		writeToFile(solution, fileName, image, subDirectory);
	}

	private void writeToFile(JavaSolution solution, GType graphType, BufferedImage image) throws GraphBuildException {
		String fileName = getFileName(solution, graphType);
		writeToFile(solution, fileName, image, null);
	}

	private void writeToFile(JavaSolution solution, String fileName, BufferedImage image, String subDirectory) throws GraphBuildException {
		try {
			String basePath = getDirectoryPath(solution, subDirectory);
			String filePath = String.format("%s/%s", basePath, fileName);
			File file = new File(filePath);
			ImageIO.write(image, IMAGE_TYPE, file);

			IILogger.info(String.format("Created graph for project [%s]! [%s]", solution.getProjectName(), filePath));
		} catch (Exception e) {
			throw new GraphBuildException(String.format("There was a problem creating the graph for project [%s]...", solution.getProjectName()), e);
		}
	}

	private String getFileName(String fileName, GType graphType) {
		return String.format("%s_%s.%s", fileName, graphType, IMAGE_TYPE);
	}

	private String getFileName(JavaSolution solution, GType graphType) {
		return String.format("%s_%s.%s", solution.getProjectName(), graphType, IMAGE_TYPE);
	}

	/**
	 * Creates the directories for metrics for this solution, if those directories do not yet exists. This also returns
	 * the full base path for this solution's directory.
	 * 
	 * @param solution
	 *            The {@link JavaSolution}
	 * @return The base path for the solution directory
	 */
	private String getDirectoryPath(JavaSolution solution, String subDirectory) {
		String filePath;
		if (subDirectory == null)
			filePath = String.format("%s/%s/%s", GRAPH_DIRECTORY_PATH, solution.getSystemName(), solution.getProjectName());
		else
			filePath = String.format("%s/%s/%s/%s", GRAPH_DIRECTORY_PATH, solution.getSystemName(), solution.getProjectName(), subDirectory);
		File metricDirectory = new File(filePath);
		metricDirectory.mkdirs();
		return filePath;
	}
}
