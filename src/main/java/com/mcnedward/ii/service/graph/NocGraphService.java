package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.NocHierarchy;
import com.mcnedward.ii.service.graph.element.Node;
import com.mcnedward.ii.service.graph.jung.JungGraph;
import com.mcnedward.ii.service.graph.jung.NocJungGraph;
import com.mcnedward.ii.utils.IILogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Edward on 9/24/2016.
 */
public class NocGraphService extends GraphService<NocHierarchy> {

    /**
     * Builds the NOC hierarchy tree. If the nocLimit is not null, then only trees with an NOC higher than the limit
     * will be created.
     *
     * @throws GraphBuildException
     */
    @Override
    protected List<JungGraph> buildGraphs(List<NocHierarchy> trees, Integer width, Integer height, Integer limit, boolean useFullName, GraphLoadListener listener) throws GraphBuildException {
        List<JungGraph> graphs = new ArrayList<>();
        Stack<Node> nodes = new Stack<>();
        Stack<Edge> edges = new Stack<>();

        for (int i = 0; i < trees.size(); i++) {
            NocHierarchy tree = trees.get(i);
            updateProgress(i, trees.size(), listener);

            if (limit != null) {
                if (tree.getNoc() < limit) continue;
            }
            String nodeName = useFullName ? tree.getFullElementName() : tree.getElementName();
            Node parentNode = new Node(tree, useFullName);
            nodes.add(parentNode);
            // Create an individual graph for each hierarchy tree
            recurseHierarchyTrees(tree, parentNode, nodes, edges, useFullName);

            JungGraph graph = new NocJungGraph(nodeName, useFullName, width, height);
            graph.plotGraph(nodes, edges);
            graphs.add(graph);

            nodes = new Stack<>();
            edges = new Stack<>();
        }
        return graphs;
    }

    @Override
    protected List<NocHierarchy> getHierarchies(JavaSolution solution) {
        return solution.getNocHierarchies();
    }

    private void recurseHierarchyTrees(NocHierarchy tree, Node parentNode, Stack<Node> nodes, Stack<Edge> edges, boolean useFullName) {
        Stack<NocHierarchy> hierarchyTree = (Stack<NocHierarchy>) tree.getTree().clone();
        while (!hierarchyTree.isEmpty()) {
            NocHierarchy childTree = hierarchyTree.pop();
            Node childNode = new Node(childTree, useFullName);
            nodes.add(childNode);
            Edge edge = new Edge(String.valueOf(childTree.getInheritedMethodCount()), parentNode, childNode);
            edge.setTitle(String.format("Inherited method count: %s", childTree.getInheritedMethodCount()));
            edges.add(edge);

            recurseHierarchyTrees(childTree, childNode, nodes, edges, useFullName);
        }
    }
}
