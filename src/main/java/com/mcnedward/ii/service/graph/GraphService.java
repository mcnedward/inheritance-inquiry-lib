package com.mcnedward.ii.service.graph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.HierarchyTree;
import com.mcnedward.ii.service.graph.element.InheritanceTree;
import com.mcnedward.ii.service.graph.element.SolutionMethod;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public class GraphService {
	private static final String GRAPH_DIRECTORY_PATH = "C:/users/edward/dev/IIGraphs";
	private static final String HIERARCHY_TREE_DIRECTORY = "hierarchy";
	private static final String IMAGE_TYPE = "png";

	public GraphService() {
	}

	public boolean buildGraphs(JavaSolution solution) {
		try {
			buildOverriddenMethodsGraph(solution);
			buildExtendedMethodsGraph(solution);
			buildInheritanceTreeGraph(solution);
			buildHierarchyTreeGraphs(solution);
			return true;
		} catch (GraphBuildException e) {
			IILogger.error(e);
			return false;
		}
	}

	private void buildOverriddenMethodsGraph(JavaSolution solution) throws GraphBuildException {
		List<SolutionMethod> methods = solution.getOMethods();
		buildMethodsGraph(solution, methods, GType.OMETHODS);
	}
	
	private void buildExtendedMethodsGraph(JavaSolution solution) throws GraphBuildException {
		List<SolutionMethod> methods = solution.getEMethods();
		buildMethodsGraph(solution, methods, GType.EMETHODS);
	}
	
	private void buildMethodsGraph(JavaSolution solution, List<SolutionMethod> methods, GType graphType) throws GraphBuildException {
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
	
	private void buildInheritanceTreeGraph(JavaSolution solution) throws GraphBuildException {
		List<InheritanceTree> trees = solution.getInheritanceTrees();
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();
		
		for (InheritanceTree hTree : trees) {
			Stack<String> tree = hTree.inheritanceTree;
			Node previousNode = null;
			while (!tree.isEmpty()) {
				String element = tree.pop();
				Node treeNode = new Node(element);
				nodes.add(treeNode);
				if (previousNode != null) {
					edges.add(new Edge("", previousNode, treeNode));
				}
				previousNode = treeNode;
			}
		}
		
		JungGraph graph = new JungGraph(500, 200);
		graph.plotGraph(nodes, edges);
		BufferedImage image = graph.createImage();
		writeToFile(solution, GType.I_TREE, image);
	}
	
	private void buildHierarchyTreeGraphs(JavaSolution solution) throws GraphBuildException {
		List<HierarchyTree> trees = solution.getHierarchyTrees();
		Stack<Node> nodes = new Stack<>();
		Stack<Edge> edges = new Stack<>();
		
		for (HierarchyTree tree : trees) {
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
	
	private void recurseHierarchyTrees(HierarchyTree tree, Node parentNode, Stack<Node> nodes, Stack<Edge> edges) {
		Stack<HierarchyTree> hierarchyTree = tree.hierarchyTrees;
		while (!hierarchyTree.isEmpty()) {
			HierarchyTree childTree = hierarchyTree.pop();
			String element = childTree.element;
			
			Node childNode = new Node(element);
			nodes.add(childNode);
			edges.add(new Edge(String.valueOf(tree.inheritedMethodCount), parentNode, childNode));
			
			recurseHierarchyTrees(childTree, childNode, nodes, edges);
		}
	}
	
	private void writeToFile(JavaSolution solution, GType graphType, BufferedImage image, String subDirectory, String name) throws GraphBuildException {
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
