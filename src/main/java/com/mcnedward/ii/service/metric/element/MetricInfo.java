package com.mcnedward.ii.service.metric.element;

import com.mcnedward.ii.service.metric.MType;

/**
 * @author Edward - Aug 13, 2016
 *
 */
public class MetricInfo {

	private MType mMetricType;
	private int mMin;
	private int mAverage;
	private int mMax;
	
	public MetricInfo(MType metricType, int min, int average, int max) {
		mMetricType = metricType;
		mMin = min;
		mAverage = average;
		mMax = max;
	}

	public MType getMetricType() {
		return mMetricType;
	}

	public int getMin() {
		return mMin;
	}

	public int getAverage() {
		return mAverage;
	}

	public int getMax() {
		return mMax;
	}

	
}
