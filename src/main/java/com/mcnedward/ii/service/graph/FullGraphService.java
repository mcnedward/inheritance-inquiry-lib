package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.FullHierarchy;
import com.mcnedward.ii.service.graph.element.Node;
import com.mcnedward.ii.service.graph.jung.JungGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * Created by Edward on 9/24/2016.
 */
public class FullGraphService extends GraphService<FullHierarchy> {

    @Override
    protected List<JungGraph> buildGraphs(List<FullHierarchy> trees, Integer width, Integer height, Integer limit, boolean ignoreZero) throws GraphBuildException {
        List<JungGraph> graphs = new ArrayList<>();
        Stack<Node> nodes = new Stack<>();
        Stack<Edge> edges = new Stack<>();

        for (FullHierarchy tree : trees) {
            String parentElement = tree.getElementName();
            Node parentNode = new Node(parentElement, tree.isInterface());
            nodes.add(parentNode);
            // Create an individual graph for each hierarchy tree
            recurseFullHierarchyTrees(tree, parentNode, nodes, edges);

            JungGraph graph = new JungGraph(tree.getFullElementName(), width, height);
            graph.plotGraph(nodes, edges);
            graphs.add(graph);

            nodes = new Stack<>();
            edges = new Stack<>();
        }
        return graphs;
    }

    @Override
    protected List<FullHierarchy> getHierarchies(JavaSolution solution) {
        return solution.getFullHierarchies();
    }

    private void recurseFullHierarchyTrees(FullHierarchy tree, Node parentNode, List<Node> nodes, List<Edge> edges) {
        Collection<FullHierarchy> subTrees = tree.getExts();
        for (FullHierarchy subclass : subTrees) {
            String element = subclass.getElementName();

            Node childNode = new Node(element, subclass.isInterface());
            nodes.add(childNode);
            edges.add(new Edge("extends", parentNode, childNode));

            recurseFullHierarchyTrees(subclass, childNode, nodes, edges);
        }
        Collection<FullHierarchy> implTrees = tree.getImpls();
        for (FullHierarchy impl : implTrees) {
            String element = impl.getElementName();

            Node childNode = new Node(element, impl.isInterface());
            nodes.add(childNode);
            edges.add(new Edge("implements", parentNode, childNode, true));

            recurseFullHierarchyTrees(impl, childNode, nodes, edges);
        }
    }
}
