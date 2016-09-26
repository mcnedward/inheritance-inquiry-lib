package com.mcnedward.ii.element;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private double mAverageWidth;
	private int mNdcMax;
	private double mNdcAverage;
	private String mMaxWidthClass;
	private List<String> mInheritedMethodRisk;
	private MetricInfo mDitMetricInfo;
	private MetricInfo mNocMetricInfo;
	private MetricInfo mWmcMetricInfo;
	private MetricInfo mOMethodInfo;
	private MetricInfo mEMethodInfo;
	private List<DitMetric> mDitMetrics;
	private List<NocMetric> mNocMetrics;
	private List<WmcMetric> mWmcMetrics;
	private Map<String, List<SolutionMethod>> mOMethods;
	private Map<String, List<SolutionMethod>> mEMethods;
	private List<DitHierarchy> mDitHierarchyTrees;
	private List<NocHierarchy> mNocHierarchyTrees;
	private List<FullHierarchy> mFullHierarchyTrees;
    private List<String> mFullyQualifiedElementNames;

	public JavaSolution(String projectName, String systemName, String version) {
        init();
        mProjectName = projectName;
        mSystemName = systemName;
        mVersion = version;
	}

	public JavaSolution(JavaProject project) {
        init();
        mProjectName = project.getName();
        mSystemName = project.getSystemName();
        mVersion = project.getVersion();
        mClassCount = project.getClassCount();
        mInheritanceCount = project.getInheritanceCount();
        mFullyQualifiedElementNames = project.getProjectFullyQualifiedElementNames();
    }

	private void init() {
		mInheritedMethodRisk = new ArrayList<>();
		mDitMetrics = new ArrayList<>();
		mNocMetrics = new ArrayList<>();
		mWmcMetrics = new ArrayList<>();
		mOMethods = new HashMap<>();
		mEMethods = new HashMap<>();
		mDitHierarchyTrees = new ArrayList<>();
		mNocHierarchyTrees = new ArrayList<>();
		mFullHierarchyTrees = new ArrayList<>();
        mFullyQualifiedElementNames = new ArrayList<>();
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
		List<SolutionMethod> methods = mOMethods.get(method.fullyQualifiedName);
		if (methods == null) {
			methods = new ArrayList<>();
			mOMethods.put(method.fullyQualifiedName, methods);
		}
		methods.add(method);
	}

	public void addEMethod(SolutionMethod method) {
		List<SolutionMethod> methods = mEMethods.get(method.fullyQualifiedName);
		if (methods == null) {
			methods = new ArrayList<>();
			mEMethods.put(method.fullyQualifiedName, methods);
		}
		methods.add(method);
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

	public Map<String, List<SolutionMethod>> getOMethods() {
		return mOMethods;
	}

	public Map<String, List<SolutionMethod>> getEMethods() {
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

	public void setAverageWidth(double averageWidth) {
		mAverageWidth = averageWidth;
	}

	public double getAverageWidth() {
		return mAverageWidth;
	}
	
	public void addInheritedMethodRisk(String risk) {
		mInheritedMethodRisk.add(risk);
	}
	
	public List<String> getInheritedMethodRisks() {
		return mInheritedMethodRisk;
	}

	public MetricInfo getMetricInfo(MType metricType) throws TaskBuildException {
		switch (metricType) {
		case DIT:
			return mDitMetricInfo;
		case NOC:
			return mNocMetricInfo;
		case WMC:
			return mWmcMetricInfo;
		case OM:
			return mOMethodInfo;
		case EM:
			return mEMethodInfo;
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
	
	public void setOMethodInfo(MetricInfo oMethodInfo) {
		mOMethodInfo = oMethodInfo;
	}
	
	public MetricInfo getOMethodInfo() {
		return mOMethodInfo;
	}
	
	public void setEMethodInfo(MetricInfo eMethodInfo) {
		mEMethodInfo = eMethodInfo;
	}
	
	public MetricInfo getEMethodInfo() {
		return mEMethodInfo;
	}
	
	public void setNdcMax(int ndcMax) {
		mNdcMax = ndcMax;
	}
	
	public int getNdcMax() {
		return mNdcMax;
	}
	
	public void setNdcAverage(double ndcAverage) {
		mNdcAverage = ndcAverage;
	}
	
	public double getNdcAverage() {
		return mNdcAverage;
	}
	
	public void setMaxWidthClass(String maxWidthClass) {
		mMaxWidthClass = maxWidthClass;
	}
	
	public String getMaxWidthClass() {
		return mMaxWidthClass;
	}

	public void setFullyQualifiedElementNames(List<String> fullyQualifiedElementNames) {
        mFullyQualifiedElementNames = fullyQualifiedElementNames;
    }

    public List<String> getFullyQualifiedElementNames() {
        return mFullyQualifiedElementNames;
    }

	@Override
	public String toString() {
		return String.format("Solution for %s", mProjectName);
	}
}
