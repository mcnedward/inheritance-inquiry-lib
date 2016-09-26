package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.GraphExportListener;
import com.mcnedward.ii.listener.GraphLoadListener;
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

    void updateProgress(int currentIndex, int graphCount, GraphLoadListener listener) {
        int progress = (int) (((double) currentIndex / graphCount) * 100);
        IILogger.notify(listener, String.format("Generating graphs [%s / %s]...", currentIndex, graphCount), progress);
    }

    @Override
    public void exportGraphsToFile(Collection<JungGraph> graphs, File directory, boolean usePackages) throws GraphBuildException {
        for (JungGraph graph : graphs)
            writeGraphToFile(graph, directory, usePackages, null);
    }

    @Override
    public void exportGraphsToFile(Collection<JungGraph> graphs, File directory, boolean usePackages, String projectName, GraphExportListener listener) throws GraphBuildException {
        if (projectName == null || projectName.equals(""))
            IILogger.notify(listener, "You need to include a project name.", 0);
        int i = 1;
        for (JungGraph graph : graphs) {
            int progress = (int) (((double) i / graphs.size()) * 100);
            IILogger.notify(listener, String.format("Generating graphs [%s / %s]...", i, graphs.size()), progress);
            writeGraphToFile(graph, directory, usePackages, projectName);
            i++;
        }
        listener.onGraphsExport();
    }

    private void writeGraphToFile(JungGraph graph, File directory, boolean usePackages, String projectName) throws GraphBuildException {
        try {
            File graphFile = buildGraphFile(directory, graph.getFullyQualifiedElementName(), usePackages, projectName);
            ImageIO.write(graph.createImage(), IMAGE_TYPE, graphFile);
            IILogger.debug(String.format("Created graph for %s! [%s]", graph.getFullyQualifiedElementName(), graphFile.getAbsoluteFile()));
        } catch (Exception e) {
            throw new GraphBuildException(String.format("There was a problem creating the graph for %s...", graph.getFullyQualifiedElementName()), e);
        }
    }

    /**
     * Creates the {@link File} for the {@link JungGraph}, optionally using the package structure of the element and the project name.
     *
     * @param directory          The {@link File} directory to export a graph to.
     * @param fullyQualifiedName The fully qualified name of the element for the graph
     * @param usePackages        True if the directory for this export should maintain the package structure of the
     *                           element
     * @param projectName        The name of the project. If this is not null, the directory {@link File} will have the
     *                           project name added.
     * @return The base path for the solution directory
     */
    private File buildGraphFile(File directory, String fullyQualifiedName, boolean usePackages, String projectName) throws GraphBuildException {
        String directoryPath = directory.getAbsolutePath();
        if (projectName != null)
            directoryPath += "/" + projectName;
        String[] elements = fullyQualifiedName.split("\\.");

        if (usePackages)
            for (int i = 0; i < elements.length; i++) {
                String element = elements[i];
                if (i != elements.length - 1) {
                    directoryPath += "/" + element;
                }
            }

        File graphDirectory = new File(directoryPath);
        if (!graphDirectory.exists()) {
            if (!graphDirectory.mkdirs())
                throw new GraphBuildException("Could not build the directory at: " + directoryPath);
        }
        String fileName = String.format("%s.%s", elements[elements.length - 1], IMAGE_TYPE);
        return new File(directoryPath + "/" + fileName);
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
