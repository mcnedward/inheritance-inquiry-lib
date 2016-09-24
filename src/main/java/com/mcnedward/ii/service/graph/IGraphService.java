package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;

import java.util.Collection;
import java.util.List;

/**
 * Created by Edward on 9/24/2016.
 */
public interface IGraphService {

    List<JungGraph> buildHierarchyGraphs(JavaSolution solution) throws GraphBuildException;

    List<JungGraph> buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames) throws GraphBuildException;

    List<JungGraph> buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height) throws GraphBuildException;

    List<JungGraph> buildHierarchyGraphs(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height, Integer limit) throws GraphBuildException;

    void writeGraphToFile(JavaSolution solution, JungGraph graph, String directoryPath) throws GraphBuildException;
}
