package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.builder.ProjectBuilder;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public abstract class IIBaseWork {
	private ProjectService mProjectService;
	private AnalyzerService mAnalyzerService;
	private MetricService mMetricService;
	private GraphService mGraphService;

	private File mProjectFile;
	private String mSystemName;
	private int mTotalJobs;
	private ProjectBuildListener mListener;

	public IIBaseWork(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs) {
		mProjectService = projectService;
		mAnalyzerService = analyzerService;
		mMetricService = metricService;
		mGraphService = graphService;
		mProjectFile = projectFile;
		mSystemName = systemName;
		mTotalJobs = totalJobs;
	}

	public IIBaseWork(ProjectService projectService, AnalyzerService analyzerService, MetricService metricService, GraphService graphService,
			File projectFile, String systemName, int totalJobs, ProjectBuildListener listener) {
		this(projectService, analyzerService, metricService, graphService, projectFile, systemName, totalJobs);
		mListener = listener;
	}

	protected abstract String getName();

	protected void setup() {
		IILogger.info("Starting job for file %s in system %s", name(), mSystemName);
	}
	
	protected JavaProject buildProject() {
		return mProjectService.build(mProjectFile, mSystemName, mListener);
	}
	
	protected void finish(JavaProject project) {
		synchronized (ProjectBuilder.COMPLETE_JOBS) {
			IILogger.info("Finished build job for %s in system %s [%s/%s]", project.toString(), mSystemName, ++ProjectBuilder.COMPLETE_JOBS,
					mTotalJobs);
		}
	}
	
	protected void error(Exception e) {
		IILogger.error(String.format("There was a problem running the task for file %s...", mProjectFile.getName()), e);
	}
	
	protected AnalyzerService analyzerService() {
		return mAnalyzerService;
	}

	protected MetricService metricService() {
		return mMetricService;
	}

	protected GraphService graphService() {
		return mGraphService;
	}
	
	protected File projectFile() {
		return mProjectFile;
	}

	protected String name() {
		return mProjectFile.getName();
	}

	@Override
	public String toString() {
		return getName();
	}
}
