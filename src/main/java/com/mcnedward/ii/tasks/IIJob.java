package com.mcnedward.ii.tasks;

import java.io.File;

import com.mcnedward.ii.builder.Builder;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.service.AnalyzerService;
import com.mcnedward.ii.service.ProjectService;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.metric.MetricService;
import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.ServiceFactory;

/**
 * @author Edward - Jul 22, 2016
 *
 */
public abstract class IIJob<T> implements Job<T> {

	private static int JOB_ID = 1;
	private int mId;

	private ProjectService mProjectService;
	private AnalyzerService mAnalyzerService;
	private MetricService mMetricService;
	private GraphService mGraphService;
	private File mProjectFile;
	private String mSystemName;
	private ProjectBuildListener mListener;

	public IIJob(File projectFile, String systemName) {
		this(projectFile, systemName, null);
	}

	public IIJob(File projectFile, String systemName, ProjectBuildListener listener) {
		mId = JOB_ID++;
		mProjectService = ServiceFactory.projectService();
		mAnalyzerService = ServiceFactory.analyzerService();
		mMetricService = ServiceFactory.metricService();
		mGraphService = ServiceFactory.graphService();
		mProjectFile = projectFile;
		mSystemName = systemName;
		mListener = listener;
	}

	@Override
	public T call() {
		IILogger.info("Starting job for %s...", mSystemName);
		try {
			JavaSolution solution = buildProject();
			T result = processSolution(solution);

			Builder.markTaskDone(this);
			IILogger.info("Completed job for %s...", mSystemName);
			return result;
		} catch (Exception e) {
			IILogger.error(String.format("Error in job for %s...", mSystemName), e);
			return null;
		}
	}

	private JavaSolution buildProject() {
		// Build project here so it doesn't stay in memory after this method, as long as any references from it are not
		// kept in the analyze() method
		JavaProject project = mProjectService.build(mProjectFile, mSystemName, mListener);
		JavaSolution solution = analyze(project);
		project = null;	// I don't think this works for garbage collection...
		return solution;		
	}

	protected abstract T processSolution(JavaSolution solution) throws TaskBuildException, GraphBuildException;

	protected JavaSolution analyze(JavaProject project) {
		return mAnalyzerService.analyze(project);
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

	@Override
	public String name() {
		return toString() + "-" + id();
	}

	@Override
	public int id() {
		return mId;
	}

	@Override
	public String toString() {
		return "SolutionTask-" + mSystemName;
	}

}
