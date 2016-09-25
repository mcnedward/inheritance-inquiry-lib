package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.Node;
import com.mcnedward.ii.service.graph.jung.DitJungGraph;
import com.mcnedward.ii.service.graph.jung.JungGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 9/24/2016.
 */
public class DitGraphService extends GraphService<DitHierarchy> {

    @Override
    protected List<JungGraph> buildGraphs(List<DitHierarchy> trees, Integer width, Integer height, Integer limit, boolean useFullName) throws GraphBuildException {
        List<JungGraph> graphs = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        for (DitHierarchy tree : trees) {
            if (tree.getDit() == 0 || tree.isInterface() || (limit != null && tree.getDit() < limit))
                continue;
            // Skip elements that have generic parameters
            // TODO This is messy, and should be fixed in the Visitors, but I don't have time for that now...
            if (tree.getElementName().contains("<") && tree.getElementName().contains(">"))
                continue;

            Node parent = new Node(tree, useFullName);
            nodes.add(parent);
            recurseDit(tree.getAncestors(), nodes, edges, parent, useFullName);

            JungGraph graph = new DitJungGraph(tree.getFullElementName(), width, height);
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
            edges.add(edge);

            recurseDit(ditH.getAncestors(), nodes, edges, hierarchyNode, useFullName);
        }
    }

}
