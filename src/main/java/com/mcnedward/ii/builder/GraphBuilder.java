package com.mcnedward.ii.builder;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.listener.GraphExportListener;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.IGraphService;
import com.mcnedward.ii.service.graph.element.GraphOptions;
import com.mcnedward.ii.service.graph.jung.JungGraph;

import java.io.File;
import java.util.Collection;

/**
 * Created by Edward on 9/25/2016.
 */
public class GraphBuilder extends Builder {

    private GraphLoadListener mLoadListener;
    private GraphExportListener mExportListener;
    private IGraphService mGraphService;
    private Collection<JungGraph> mGraphs;
    private File mDirectory;
    private boolean mUsePackages;
    private String mProjectName;
    private GraphOptions mOptions;
    private boolean mIsBuild, mIsExport;

    public GraphBuilder(GraphLoadListener listener) {
        super();
        mLoadListener = listener;
    }

    public GraphBuilder(GraphExportListener listener) {
        super();
        mExportListener = listener;
    }

    public GraphBuilder(GraphLoadListener graphLoadListener, GraphExportListener graphExportListener) {
        super();
        mLoadListener = graphLoadListener;
        mExportListener = graphExportListener;
    }

    public GraphBuilder setupForBuild(IGraphService service, GraphOptions options) {
        mGraphService = service;
        mOptions = options;
        mIsBuild = true;
        return this;
    }

    public GraphBuilder setupForExport(IGraphService service, Collection<JungGraph> graphs, File directory, boolean usePackages, String projectName) {
        mGraphService = service;
        mGraphs = graphs;
        mDirectory = directory;
        mUsePackages = usePackages;
        mProjectName = projectName;
        mIsExport = true;
        return this;
    }

    @Override
    protected Runnable buildTask() {
        if (mIsBuild && mIsExport) {
            throw new IllegalStateException("You can only call the setup for one process.");
        }
        if (mIsBuild) {
            if (mGraphService == null || mOptions == null) {
                throw new IllegalStateException("You need to call setup method first!");
            }
            return () -> {
                try {
                    mGraphService.buildHierarchyGraphs(mOptions, mLoadListener);
                } catch (Exception e) {
                    mLoadListener.onBuildError(String.format("Something went wrong when building the graphs."), e);
                } finally {
                    reset();
                }
            };
        }
        if (mIsExport) {
            if (mGraphService == null) {
                throw new IllegalStateException("You need to call setup method first!");
            }
            return () -> {
                try {
                    mGraphService.exportGraphsToFile(mGraphs, mDirectory, mUsePackages, mProjectName, mExportListener);
                } catch (GraphBuildException e) {
                    mLoadListener.onBuildError(String.format("Something went wrong when exporting the graphs."), e);
                } finally {
                    reset();
                }
            };
        }
        throw new IllegalStateException("You need to call setup method first!");
    }

    @Override
    protected void reset() {
        mGraphService = null;
        mOptions = null;
        mGraphs = null;
        mDirectory = null;
        mUsePackages = false;
        mProjectName = null;
        mIsBuild = false;
        mIsExport = false;
    }
}
