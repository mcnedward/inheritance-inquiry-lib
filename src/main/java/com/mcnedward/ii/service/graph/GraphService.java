package com.mcnedward.ii.service.graph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public class GraphService {
	private static final String GRAPH_DIRECTORY_PATH = "C:/users/edward/dev/IIGraphs";
	private static final String IMAGE_TYPE = "png";

	public GraphService() {
	}

	public boolean buildGraphs(JavaSolution solution) {
		try {
			buildOverriddenMethodsGraph(solution);
			buildExtendedMethodsGraph(solution);
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
			Node methodNode = new MethodNode(method.methodSignature);
			Node parentNode = new MethodNode(method.parentElementName);
			Node elementNode = new MethodNode(method.elementName);

			Edge edge1 = new MethodEdge("Parent", methodNode, parentNode);
			Edge edge2 = new MethodEdge("Element", methodNode, elementNode);

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
	
	private String getFileName(JavaSolution solution, GType graphType) {
		return String.format("%s_%s_%s.%s", solution.getProjectName(), solution.getVersion(), graphType, IMAGE_TYPE);
	}
	
	private void writeToFile(JavaSolution solution, GType graphType, BufferedImage image) throws GraphBuildException {
		String fileName = getFileName(solution, graphType);
		
		try {
			String basePath = getDirectoryPath(solution);
			String filePath = String.format("%s/%s", basePath, fileName);
			File file = new File(filePath);
			ImageIO.write(image, IMAGE_TYPE, file);
			
			IILogger.info(String.format("Created graph for project [%s]! [%s]", solution.getProjectName(), filePath));
		} catch (Exception e) {
			throw new GraphBuildException(String.format("There was a problem creating the graph for project [%s]...", solution.getProjectName()));
		}
	}
	
	/**
	 * Creates the directories for metrics for this solution, if those directories do not yet exists. This also returns
	 * the full base path for this solution's directory.
	 * 
	 * @param solution
	 *            The {@link JavaSolution}
	 * @return The base path for the solution directory
	 */
	private String getDirectoryPath(JavaSolution solution) {
		String filePath = String.format("%s/%s/%s", GRAPH_DIRECTORY_PATH, solution.getSystemName(), solution.getProjectName());
		File metricDirectory = new File(filePath);
		metricDirectory.mkdirs();
		return filePath;
	}
}
