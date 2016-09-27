package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.GraphOptions;
import com.mcnedward.ii.service.graph.element.Node;
import com.mcnedward.ii.service.graph.jung.JungGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 9/24/2016.
 */
public class DitGraphService extends GraphService<DitHierarchy> {

    @Override
    protected List<JungGraph> buildGraphs(List<DitHierarchy> trees, GraphOptions options, GraphLoadListener listener) throws GraphBuildException {
        List<JungGraph> graphs = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        Integer limit = options.getLimit();
        boolean useFullName = options.useFullName();
        for (int i = 0; i < trees.size(); i++) {
            DitHierarchy tree = trees.get(i);
            updateProgress(i + 1, trees.size(), listener);

            if (tree.getDit() == 1 || tree.isInterface() || (limit != null && tree.getDit() < limit))
                continue;
            // Skip elements that have generic parameters
            // TODO This is messy, and should be fixed in the Visitors, but I don't have time for that now...
//            if (tree.getElementName().contains("<") && tree.getElementName().contains(">"))
//                continue;

            Node parent = new Node(tree, useFullName);
            nodes.add(parent);
            recurseDit(tree.getAncestors(), nodes, edges, parent, useFullName);

            JungGraph graph = new JungGraph(tree.getFullElementName(), options);
            graph.plotGraph(nodes, edges);
            graphs.add(graph);

            nodes.clear();
            edges.clear();
        }
        return graphs;
    }

    @Override
    protected List<DitHierarchy> getHierarchies(JavaSolution solution) {
        return solution.getDitHierarchies();
    }

    private void recurseDit(List<DitHierarchy> ancestors, List<Node> nodes, List<Edge> edges, Node parentNode, boolean useFullName) {
        if (ancestors.isEmpty())
            return;
        for (DitHierarchy ditH : ancestors) {
            Node hierarchyNode = new Node(ditH, useFullName);
            nodes.add(hierarchyNode);

            Edge edge = new Edge(String.valueOf(ditH.getInheritedMethodCount()), hierarchyNode, parentNode);
            edge.setTitle(String.format("Inherited method count: %s", ditH.getInheritedMethodCount()));
            edges.add(edge);

            recurseDit(ditH.getAncestors(), nodes, edges, hierarchyNode, useFullName);
        }
    }

}
