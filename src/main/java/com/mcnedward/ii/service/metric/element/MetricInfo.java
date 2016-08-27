package com.mcnedward.ii.service.metric.element;

import java.util.List;

import com.mcnedward.ii.service.metric.MType;

/**
 * TODO: This is being used for metrics and methods, need to probably redo this later on
 * @author Edward - Aug 13, 2016
 *
 */
public class MetricInfo {

	private MType mMetricType;
	private int mMin;
	private double mAverage;
	private int mMax;
	private List<String> mMaxClasses;
	
	public MetricInfo(int min, double average, int max, List<String> maxClasses) {
		mMin = min;
		mAverage = average;
		mMax = max;
		mMaxClasses = maxClasses;
	}
	
	public MetricInfo(MType metricType, int min, double average, int max, List<String> maxClasses) {
		mMetricType = metricType;
		mMin = min;
		mAverage = average;
		mMax = max;
		mMaxClasses = maxClasses;
	}

	public MType getMetricType() {
		return mMetricType;
	}

	public int getMin() {
		return mMin;
	}

	public double getAverage() {
		return mAverage;
	}

	public int getMax() {
		return mMax;
	}

	public List<String> getMaxClasses() {
		return mMaxClasses;
	}
	
	@Override
	public String toString() {
		return String.format("Average[%s] - Max[%s] - MaxClass[%s]", mAverage, mMax, mMaxClasses);
	}
	
}
