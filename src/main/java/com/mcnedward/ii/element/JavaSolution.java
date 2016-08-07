package com.mcnedward.ii.element;

import java.util.ArrayList;
import java.util.List;

import com.mcnedward.ii.service.graph.element.NocHierarchy;
import com.mcnedward.ii.service.graph.element.DitHierarchy;
import com.mcnedward.ii.service.graph.element.SolutionMethod;
import com.mcnedward.ii.service.metric.element.DitMetric;
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
	private List<DitMetric> mDitMetrics;
	private List<NocMetric> mNocMetrics;
	private List<WmcMetric> mWmcMetrics;
	private List<SolutionMethod> mOMethods;
	private List<SolutionMethod> mEMethods;
	private List<DitHierarchy> mDitHierarchyTrees;
	private List<NocHierarchy> mNocHierarchyTrees;

	public JavaSolution(String projectName, String systemName, String version) {
		mProjectName = projectName;
		mSystemName = systemName;
		mVersion = version;
		mDitMetrics = new ArrayList<>();
		mNocMetrics = new ArrayList<>();
		mWmcMetrics = new ArrayList<>();
		mOMethods = new ArrayList<>();
		mEMethods = new ArrayList<>();
		mDitHierarchyTrees = new ArrayList<>();
		mNocHierarchyTrees = new ArrayList<>();
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
	
	@Override
	public String toString() {
		return String.format("Solution for %s", mProjectName);
	}
}
