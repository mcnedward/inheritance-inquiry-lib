package com.mcnedward.ii.builder;

import com.mcnedward.ii.exception.MetricExportException;
import com.mcnedward.ii.listener.MetricExportListener;
import com.mcnedward.ii.service.metric.IMetricService;
import com.mcnedward.ii.service.metric.element.MetricOptions;

/**
 * Created by Edward on 9/25/2016.
 */
public class MetricBuilder extends Builder {

    private MetricExportListener mExportListener;
    private IMetricService mMetricService;
    private MetricOptions mOptions;

    public MetricBuilder(MetricExportListener listener) {
        super();
        mExportListener = listener;
    }

    public MetricBuilder setup(IMetricService service, MetricOptions options) {
        mMetricService = service;
        mOptions = options;
        return this;
    }

    @Override
    protected Runnable buildTask() {
        if (mMetricService == null || mOptions == null || mOptions.getSolution() == null) {
            throw new IllegalStateException("You need to call setup method first, and be sure to include a JavaSolution!");
        }
        return () -> {
            try {
                mMetricService.exportMetrics(mOptions, mExportListener);
            } catch (MetricExportException e) {
                mExportListener.onBuildError(String.format("Something went wrong when exporting the metrics."), e);
            } finally {
                reset();
            }
        };
    }

    @Override
    protected void reset() {
        mMetricService = null;
        mOptions = null;
    }
}
