package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.ProjectBuilder;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.utils.Constants;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 16, 2016
 *
 */
public class ProjectBuildTask implements Runnable {

	private ProjectService mBuilder;
	private MetricService mMetricBuilder;
	
	private File mProjectFile;
	private String mSystemName;
	private int mTotalJobs;

	public ProjectBuildTask(File projectFile, String systemName, int totalJobs) {
		mProjectFile = projectFile;
		mSystemName = systemName;
		mTotalJobs = totalJobs;
		mBuilder = new ProjectService();
		mMetricBuilder = new MetricService(Constants.METRIC_DIRECTORY_PATH);
	}

	@Override
	public void run() {
		IILogger.info("Starting build job for file %s in system %s", mProjectFile.getName(), mSystemName);

		try {
			JavaProject project = mBuilder.build(mProjectFile, mSystemName);
			
			mMetricBuilder.buildMetrics(project);
			
			synchronized (ProjectBuilder.COMPLETE_JOBS) {
				IILogger.info("Finished build job for %s in system %s [%s/%s]", project.toString(), mSystemName,
						++ProjectBuilder.COMPLETE_JOBS, mTotalJobs);
			}
		} catch (Exception e) {
			IILogger.error(String.format("There was a problem running the task for file %s...", mProjectFile.getName()), e);
		}
	}
	
	public String getName() {
		return "ProjectBuildTask-" + mProjectFile.getName();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
