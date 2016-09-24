package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.GType;
import com.mcnedward.ii.service.graph.element.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 9/24/2016.
 */
public class DitGraphService extends GraphService<DitHierarchy> {

    @Override
    protected List<JungGraph> buildGraphs(List<DitHierarchy> trees, Integer width, Integer height, Integer limit) throws GraphBuildException {
        List<JungGraph> graphs = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        for (DitHierarchy hierarchy : trees) {
            if (hierarchy.getDit() == 0 || hierarchy.isInterface() || (limit != null && hierarchy.getDit() < limit))
                continue;
            // Skip elements that have generic parameters
            // TODO This is messy, and should be fixed in the Visitors, but I don't have time for that now...
            if (hierarchy.getElementName().contains("<") && hierarchy.getElementName().contains(">"))
                continue;

            Node parent = new Node(hierarchy.getElementName());
            nodes.add(parent);
            recurseDit(hierarchy.getAncestors(), nodes, edges, parent);

            JungGraph graph = new JungGraph(hierarchy.getFullElementName(), width, height);
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

    private void recurseDit(List<DitHierarchy> ancestors, List<Node> nodes, List<Edge> edges, Node parentNode) {
        if (ancestors.isEmpty())
            return;
        for (DitHierarchy ditH : ancestors) {
            Node hierarchyNode = new Node(ditH.getElementName());
            nodes.add(hierarchyNode);

            Edge edge = new Edge(String.valueOf(ditH.getInheritedMethodCount()), hierarchyNode, parentNode);
            edges.add(edge);

            recurseDit(ditH.getAncestors(), nodes, edges, hierarchyNode);
        }
    }

}
