package com.mcnedward.ii.element;

import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.FullHierarchy;
import com.mcnedward.ii.service.graph.element.NocHierarchy;
import com.mcnedward.ii.service.graph.element.SolutionMethod;
import com.mcnedward.ii.service.metric.MType;
import com.mcnedward.ii.service.metric.element.DitMetric;
import com.mcnedward.ii.service.metric.element.MetricInfo;
import com.mcnedward.ii.service.metric.element.NocMetric;
import com.mcnedward.ii.service.metric.element.WmcMetric;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public class JavaSolution {

	private String mProjectName;
	private String mSystemName;
	private String mVersion;
	private int mClassCount;
	private int mInheritanceCount;
	private int mMaxWidth;
	private int mAverageWidth;
	private MetricInfo mDitMetricInfo;
	private MetricInfo mNocMetricInfo;
	private MetricInfo mWmcMetricInfo;
	private List<DitMetric> mDitMetrics;
	private List<NocMetric> mNocMetrics;
	private List<WmcMetric> mWmcMetrics;
	private List<SolutionMethod> mOMethods;
	private List<SolutionMethod> mEMethods;
	private List<DitHierarchy> mDitHierarchyTrees;
	private List<NocHierarchy> mNocHierarchyTrees;
	private List<FullHierarchy> mFullHierarchyTrees;

	public JavaSolution(String projectName, String systemName, String version) {
		mProjectName = projectName;
		mSystemName = systemName;
		mVersion = version;
		init();
	}

	public JavaSolution(JavaProject project) {
		mProjectName = project.getName();
		mSystemName = project.getSystemName();
		mVersion = project.getVersion();
		mClassCount = project.getClassCount();
		mInheritanceCount = project.getInheritanceCount();
		init();
	}

	private void init() {
		mDitMetrics = new ArrayList<>();
		mNocMetrics = new ArrayList<>();
		mWmcMetrics = new ArrayList<>();
		mOMethods = new ArrayList<>();
		mEMethods = new ArrayList<>();
		mDitHierarchyTrees = new ArrayList<>();
		mNocHierarchyTrees = new ArrayList<>();
		mFullHierarchyTrees = new ArrayList<>();
	}

	public void addDitMetric(DitMetric metric) {
		mDitMetrics.add(metric);
	}

	public void addNocMetric(NocMetric metric) {
		mNocMetrics.add(metric);
	}

	public void addWmcMetric(WmcMetric metric) {
		mWmcMetrics.add(metric);
	}

	public void addOMethod(SolutionMethod method) {
		mOMethods.add(method);
	}

	public void addEMethod(SolutionMethod method) {
		mEMethods.add(method);
	}

	public void addDitHierarchy(DitHierarchy tree) {
		mDitHierarchyTrees.add(tree);
	}

	public void addNocHeirarchy(NocHierarchy tree) {
		mNocHierarchyTrees.add(tree);
	}

	public void addFullHierarchy(FullHierarchy tree) {
		mFullHierarchyTrees.add(tree);
	}

	public String getProjectName() {
		return mProjectName;
	}

	public String getSystemName() {
		return mSystemName;
	}

	public String getVersion() {
		return mVersion;
	}

	public List<DitMetric> getDitMetrics() {
		return mDitMetrics;
	}

	public List<NocMetric> getNocMetrics() {
		return mNocMetrics;
	}

	public List<WmcMetric> getWmcMetrics() {
		return mWmcMetrics;
	}

	public List<SolutionMethod> getOMethods() {
		return mOMethods;
	}

	public List<SolutionMethod> getEMethods() {
		return mEMethods;
	}

	public List<DitHierarchy> getDitHierarchies() {
		return mDitHierarchyTrees;
	}

	public List<NocHierarchy> getNocHierarchies() {
		return mNocHierarchyTrees;
	}

	public List<FullHierarchy> getFullHierarchies() {
		return mFullHierarchyTrees;
	}

	public void setInheritanceCount(int inheritanceCount) {
		mInheritanceCount = inheritanceCount;
	}

	public int getClassCount() {
		return mClassCount;
	}

	public int getInheritanceCount() {
		return mInheritanceCount;
	}

	public void setMaxWidth(int maxWidth) {
		mMaxWidth = maxWidth;
	}

	public int getMaxWidth() {
		return mMaxWidth;
	}

	public void setAverageWidth(int averageWidth) {
		mAverageWidth = averageWidth;
	}

	public int getAverageWidth() {
		return mAverageWidth;
	}

	public MetricInfo getMetricInfo(MType metricType) throws TaskBuildException {
		switch (metricType) {
		case DIT:
			return mDitMetricInfo;
		case NOC:
			return mNocMetricInfo;
		case WMC:
			return mWmcMetricInfo;
		default:
			throw new TaskBuildException("Metric type " + metricType.name() + " is not acceptable for inquiry...");
		}
	}
	
	public void setDitMetricInfo(MetricInfo ditMetricInfo) {
		mDitMetricInfo = ditMetricInfo;
	}

	public MetricInfo getDitMetricInfo() {
		return mDitMetricInfo;
	}

	public void setNocMetricInfo(MetricInfo nocMetricInfo) {
		mNocMetricInfo = nocMetricInfo;
	}

	public MetricInfo getNocMetricInfo() {
		return mNocMetricInfo;
	}

	public void setWmcMetricInfo(MetricInfo wmcMetricInfo) {
		mWmcMetricInfo = wmcMetricInfo;
	}

	public MetricInfo getWmcMetricInfo() {
		return mWmcMetricInfo;
	}

	@Override
	public String toString() {
		return String.format("Solution for %s", mProjectName);
	}
}
