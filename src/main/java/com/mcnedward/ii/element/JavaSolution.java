package com.mcnedward.ii.element;

import java.util.List;

import com.mcnedward.ii.service.graph.SolutionMethod;
import com.mcnedward.ii.service.metric.DitMetric;
import com.mcnedward.ii.service.metric.NocMetric;
import com.mcnedward.ii.service.metric.WmcMetric;

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
	
	public JavaSolution(String projectName, String systemName, String version, List<DitMetric> ditMetrics, List<NocMetric> nocMetrics,
			List<WmcMetric> wmcMetrics, List<SolutionMethod> oMethods, List<SolutionMethod> eMethods) {
		mProjectName = projectName;
		mSystemName = systemName;
		mVersion = version;
		mDitMetrics = ditMetrics;
		mNocMetrics = nocMetrics;
		mWmcMetrics = wmcMetrics;
		mOMethods = oMethods;
		mEMethods = eMethods;
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

}
