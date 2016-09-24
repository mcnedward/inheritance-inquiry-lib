package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.NocHierarchy;
import com.mcnedward.ii.service.graph.element.Node;

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
    protected List<JungGraph> buildGraphs(List<NocHierarchy> trees, Integer width, Integer height, Integer limit) throws GraphBuildException {
        List<JungGraph> graphs = new ArrayList<>();
        Stack<Node> nodes = new Stack<>();
        Stack<Edge> edges = new Stack<>();

        for (NocHierarchy tree : trees) {
            if (limit != null) {
                if (tree.getNoc() < limit) continue;
            }
            String parentElement = tree.getFullElementName();
            Node parentNode = new Node(parentElement);
            nodes.add(parentNode);
            // Create an individual graph for each hierarchy tree
            recurseHierarchyTrees(tree, parentNode, nodes, edges);

            JungGraph graph = new JungGraph(tree.getFullElementName(), width, height);
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

    private void recurseHierarchyTrees(NocHierarchy tree, Node parentNode, Stack<Node> nodes, Stack<Edge> edges) {
        Stack<NocHierarchy> hierarchyTree = tree.getTree();
        while (!hierarchyTree.isEmpty()) {
            NocHierarchy childTree = hierarchyTree.pop();
            String element = childTree.getFullElementName();

            Node childNode = new Node(element);
            nodes.add(childNode);
            edges.add(new Edge(String.valueOf(childTree.getInheritedMethodCount()), parentNode, childNode));

            recurseHierarchyTrees(childTree, childNode, nodes, edges);
        }
    }
}
