package com.mcnedward.ii;

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
		buildSystem();
	}
	
	protected static void buildSystem() {
		try {
			IILogger.DEBUG = true;
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
	
}
