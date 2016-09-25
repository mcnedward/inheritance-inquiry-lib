package com.mcnedward.ii.service.graph.jung;

/**
 * Created by Edward on 9/25/2016.
 */
public class DitJungGraph extends JungGraph {

    private static final int X_DIST = 300;

    public DitJungGraph(String fullyQualifiedName, Integer width, Integer height) {
        super(fullyQualifiedName, width, height, X_DIST, null);
    }

}
