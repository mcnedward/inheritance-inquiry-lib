package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.element.GType;
import com.mcnedward.ii.service.graph.element.Hierarchy;
import com.mcnedward.ii.service.graph.jung.JungGraph;
import com.mcnedward.ii.utils.IILogger;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public abstract class GraphService<T extends Hierarchy> implements IGraphService {
	private static final String IMAGE_TYPE = "png";

	@Override
	public void buildHierarchyGraphs(JavaSolution solution) throws GraphBuildException {
		buildHierarchyGraphs(solution, null, null, null, null, false, null);
	}

    @Override
    public void buildHierarchyGraphs(JavaSolution solution, GraphLoadListener listener) throws GraphBuildException {
        buildHierarchyGraphs(solution, null, null, null, null, false, listener);
    }

    @Override
    public void buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames) throws GraphBuildException {
        buildHierarchyGraphs(solution, fullyQualifiedNames, null, null, null, false, null);
    }

    @Override
    public void buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height) throws GraphBuildException {
        buildHierarchyGraphs(solution, fullyQualifiedNames, width, height, null, false, null);
    }

    @Override
    public void buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height, boolean useFullName, GraphLoadListener listener) throws GraphBuildException {
        buildHierarchyGraphs(solution, fullyQualifiedNames, width, height, null, useFullName, listener);
    }

    @Override
    public void buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height, Integer limit, boolean useFullName, GraphLoadListener listener) throws GraphBuildException {
        IILogger.notify(listener, "Building graphs...", 0);

        List<T> trees;
        if (fullyQualifiedNames == null) {
            trees = getHierarchies(solution);
        } else {
            trees = new ArrayList<>();
            for (T hierachy : getHierarchies(solution)) {
                if (trees.size() == fullyQualifiedNames.size())
                    break;
                if (fullyQualifiedNames.contains(hierachy.getFullElementName())) {
                    trees.add(hierachy);
                    continue;
                }
            }
            if (trees.size() != fullyQualifiedNames.size()) {
                throw new GraphBuildException("Could not find the all of the specified metrics...");
            }
        }
        List<JungGraph> graphs = buildGraphs(trees, width, height, limit, useFullName, listener);
        listener.onGraphsLoaded(graphs);
    }

    protected abstract List<T> getHierarchies(JavaSolution solution);

    protected abstract List<JungGraph> buildGraphs(List<T> trees, Integer width, Integer height, Integer limit, boolean useFullName, GraphLoadListener listener) throws GraphBuildException;

    protected void updateProgress(int currentIndex, int graphCount, GraphLoadListener listener) {
        int progress = (int) (((double) currentIndex / graphCount) * 100);
        IILogger.notify(listener, String.format("Generating graphs [%s / %s]...", currentIndex, graphCount), progress);
    }

    @Override
    public void writeGraphToFile(JavaSolution solution, JungGraph graph, String directoryPath) throws GraphBuildException {
        String fileName = getFileName(graph.getElementName(), graph.getType());
        try {
            String basePath = getDirectoryPath(solution, directoryPath);
            String filePath = String.format("%s/%s", basePath, fileName);
            File file = new File(filePath);
            ImageIO.write(graph.createImage(), IMAGE_TYPE, file);

            IILogger.debug(String.format("Created graph for project [%s]! [%s]", solution.getProjectName(), filePath));
        } catch (Exception e) {
            throw new GraphBuildException(String.format("There was a problem creating the graph for project [%s]...", solution.getProjectName()), e);
        }
    }

	private String getFileName(String fileName, GType graphType) {
		return String.format("%s_%s.%s", fileName, graphType, IMAGE_TYPE);
	}

	/**
	 * Creates the directories for metrics for this solution, if those directories do not yet exists. This also returns
	 * the full base path for this solution's directory.
	 * 
	 * @param solution
	 *            The {@link JavaSolution}
	 * @return The base path for the solution directory
	 */
	private String getDirectoryPath(JavaSolution solution, String directoryPath) {
		String filePath = String.format("%s/%s/%s", directoryPath, solution.getSystemName(), solution.getProjectName());
		File metricDirectory = new File(filePath);
		metricDirectory.mkdirs();
		return filePath;
	}

//	public void buildExtendedMethodsGraph(JavaSolution solution) throws GraphBuildException {
//		IILogger.info("Building graph for extended methods in solution %s...", solution.getSystemName());
//		List<SolutionMethod> methods = new ArrayList<>();
//		for (Map.Entry<String, List<SolutionMethod>> entry : solution.getEMethods().entrySet()) {
//			methods.addAll(entry.getValue());
//		}
//		buildMethodsGraph(solution, methods, GType.EMETHODS);
//	}
//
//	public void buildMethodsGraph(JavaSolution solution, Collection<SolutionMethod> methods, GType graphType) throws GraphBuildException {
//		IILogger.info("Building graph for solution methods in solution %s...", solution.getSystemName());
//		List<Node> nodes = new ArrayList<>();
//		List<Edge> edges = new ArrayList<>();
//
//		for (SolutionMethod method : methods) {
//			Node methodNode = new Node(method.methodSignature);
//			Node parentNode = new Node(method.parentElementName);
//			Node elementNode = new Node(method.elementName);
//
//			Edge edge1 = new Edge("Parent", methodNode, parentNode);
//			Edge edge2 = new Edge("Element", methodNode, elementNode);
//
//			nodes.add(methodNode);
//			nodes.add(parentNode);
//			nodes.add(elementNode);
//
//			edges.add(edge1);
//			edges.add(edge2);
//		}
//
//		JungGraph graph = new JungGraph("Methods");
//		graph.plotGraph(nodes, edges);
//		BufferedImage image = graph.createImage();
//		writeToFile(solution, graphType, image);
//	}
}
