package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.FullHierarchy;
import com.mcnedward.ii.service.graph.element.GraphOptions;
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
    protected List<JungGraph> buildGraphs(List<FullHierarchy> trees, GraphOptions options, GraphLoadListener listener) throws GraphBuildException {
        List<JungGraph> graphs = new ArrayList<>();
        Stack<Node> nodes = new Stack<>();
        Stack<Edge> edges = new Stack<>();

        boolean useFullName = options.useFullName();
        for (int i = 0; i < trees.size(); i++) {
            FullHierarchy tree = trees.get(i);
            updateProgress(i + 1, trees.size(), listener);
            Node parentNode = new Node(tree);
            nodes.add(parentNode);
            // Create an individual graph for each hierarchy tree
            recurseFullHierarchyTrees(tree, parentNode, nodes, edges);

            JungGraph graph = new JungGraph(tree.getFullElementName(), tree.getElementName(), options);
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

            Node childNode = new Node(subclass);
            nodes.add(childNode);
            Edge edge = new Edge("extends", parentNode, childNode);
            edge.setTitle(String.format("%s extends %s", tree.getElementName(), subclass.getElementName()));
            edges.add(edge);

            recurseFullHierarchyTrees(subclass, childNode, nodes, edges);
        }
        Collection<FullHierarchy> implTrees = tree.getImpls();
        for (FullHierarchy impl : implTrees) {
            String element = impl.getElementName();

            Node childNode = new Node(impl);
            nodes.add(childNode);
            edges.add(new Edge("implements", parentNode, childNode, true));

            recurseFullHierarchyTrees(impl, childNode, nodes, edges);
        }
    }
}
