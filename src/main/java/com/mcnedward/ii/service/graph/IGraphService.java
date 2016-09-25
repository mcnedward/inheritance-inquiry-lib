package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.jung.JungGraph;

import java.util.Collection;
import java.util.List;

/**
 * Created by Edward on 9/24/2016.
 */
public interface IGraphService {

    void buildHierarchyGraphs(JavaSolution solution) throws GraphBuildException;

    void buildHierarchyGraphs(JavaSolution solution, GraphLoadListener listener) throws GraphBuildException;

    void buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames) throws GraphBuildException;

    void buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height) throws GraphBuildException;

    void buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height, boolean useFullName, GraphLoadListener listener) throws GraphBuildException;

    void buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height, Integer limit, boolean useFullName, GraphLoadListener listener) throws GraphBuildException;

    void writeGraphToFile(JavaSolution solution, JungGraph graph, String directoryPath) throws GraphBuildException;
}
