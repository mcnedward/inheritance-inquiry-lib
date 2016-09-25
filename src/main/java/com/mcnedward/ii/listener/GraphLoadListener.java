package com.mcnedward.ii.listener;

import com.mcnedward.ii.service.graph.jung.JungGraph;

import java.util.List;

/**
 * Created by Edward on 9/25/2016.
 */
public interface GraphLoadListener extends BuildListener {

    void onGraphsLoaded(List<JungGraph> graphs);
}
