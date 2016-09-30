package com.mcnedward.ii.service.metric;

import com.mcnedward.ii.exception.MetricExportException;
import com.mcnedward.ii.listener.MetricExportListener;
import com.mcnedward.ii.service.metric.element.MetricOptions;

/**
 * Created by Edward on 9/30/2016.
 */
public interface IMetricService {

    void exportMetrics(MetricOptions options, MetricExportListener listener) throws MetricExportException;
}
