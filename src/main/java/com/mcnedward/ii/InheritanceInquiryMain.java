package com.mcnedward.ii;

import org.apache.log4j.Logger;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.ProjectBuildService;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public final class InheritanceInquiryMain {
	private static final Logger logger = Logger.getLogger(InheritanceInquiryMain.class);
	
	
	public static void main(String[] args) {
		buildSystem();
	}
	
	protected static void buildSystem() {
		try {
			IILogger.DEBUG = true;
			
			JavaSystem system = new ProjectBuildService().buildAsync();
			IILogger.info("Finished building the system %s! Time to complete: %s", system.toString(), system.getBuildTime());
			
			for (JavaProject project : system.getProjects()) {
				IILogger.info(project.toString());
			}
		} catch (TaskBuildException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	protected static void buildProject() {
		new ProjectBuildService().buildProject();
	}
	
}
