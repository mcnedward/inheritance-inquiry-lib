package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.GraphExportListener;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.element.GraphOptions;
import com.mcnedward.ii.service.graph.jung.JungGraph;

import java.io.File;
import java.util.Collection;

/**
 * Created by Edward on 9/24/2016.
 */
public interface IGraphService {

    void buildHierarchyGraphs(GraphOptions options, GraphLoadListener listener) throws GraphBuildException;

    void exportGraphsToFile(Collection<JungGraph> graphs, File directory, boolean usePackages) throws GraphBuildException;

    void exportGraphsToFile(Collection<JungGraph> graphs, File directory, boolean usePackages, String projectName, GraphExportListener listener) throws GraphBuildException;
}
