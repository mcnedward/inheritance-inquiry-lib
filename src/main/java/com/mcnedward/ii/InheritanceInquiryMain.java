package com.mcnedward.ii;

import com.mcnedward.ii.builder.GraphBuilder;
import com.mcnedward.ii.builder.MetricAnalysisBuilder;
import com.mcnedward.ii.builder.ProjectBuilder;
import com.mcnedward.ii.builder.SystemBuilder;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public final class InheritanceInquiryMain {
	
	public static void main(String[] args) {
		IILogger.DEBUG = true;
		buildProject();
	}
	
	protected static void buildDitAnalysis() {
		try {
			new MetricAnalysisBuilder().build();
		} catch (TaskBuildException e) {
			IILogger.error(e.getMessage(), e);
		}
	}
	
	protected static void buildSystem() {
		try {
			new SystemBuilder().build();
		} catch (TaskBuildException e) {
			IILogger.error(e.getMessage(), e);
		}
	}
	
	protected static void buildProject() {
		try {
			new ProjectBuilder().build();
		} catch (TaskBuildException e) {
			IILogger.error(e.getMessage(), e);
		}
	}
	
	protected static void buildGraph() {
		try {
			new GraphBuilder().build();
		} catch (TaskBuildException e) {
			IILogger.error(e.getMessage(), e);
		}
	}
	
}
