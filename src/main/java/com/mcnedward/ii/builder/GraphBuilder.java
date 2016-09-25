package com.mcnedward.ii.builder;

import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.IGraphService;

import java.util.Collection;

/**
 * Created by Edward on 9/25/2016.
 */
public class GraphBuilder extends Builder {

    private GraphLoadListener mListener;
    private IGraphService mGraphService;
    private JavaSolution mSolution;
    private Collection<String> mFullyQualifiedNames;
    private Integer mWidth, mHeight;
    private boolean mUseFullName;

    public GraphBuilder(GraphLoadListener listener) {
        super();
        mListener = listener;
    }

    public GraphBuilder setup(IGraphService service, JavaSolution solution, Collection<String> fullyQualifiedNames, Integer width, Integer height, boolean useFullName) {
        mGraphService = service;
        mSolution = solution;
        mFullyQualifiedNames = fullyQualifiedNames;
        mWidth = width;
        mHeight = height;
        mUseFullName = useFullName;
        return this;
    }

    @Override
    protected Runnable buildTask() {
        if (mGraphService == null || mSolution == null) {
            throw new IllegalStateException("You need to call setup method first!");
        }
        return () -> {
            try {
                mGraphService.buildHierarchyGraphs(mSolution, mFullyQualifiedNames, mWidth, mHeight, mUseFullName, mListener);
            } catch (Exception e) {
                mListener.onBuildError(String.format("Something went wrong when building the graphs."), e);
            } finally {
                reset();
            }
        };
    }

    @Override
    protected void reset() {
        mGraphService = null;
        mSolution = null;
        mFullyQualifiedNames = null;
        mWidth = null;
        mHeight = null;
        mUseFullName = false;
    }
}
