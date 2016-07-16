package com.mcnedward.ii.element;

import java.util.List;

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
	
	public JavaSolution(String projectName, String systemName, String version, List<DitMetric> ditMetrics, List<NocMetric> nocMetrics,
			List<WmcMetric> wmcMetrics) {
		mProjectName = projectName;
		mSystemName = systemName;
		mVersion = version;
		mDitMetrics = ditMetrics;
		mNocMetrics = nocMetrics;
		mWmcMetrics = wmcMetrics;
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

}
