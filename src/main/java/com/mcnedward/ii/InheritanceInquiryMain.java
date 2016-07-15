package com.mcnedward.ii;

import org.apache.log4j.Logger;

import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.service.ProjectBuildService;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InheritanceInquiryMain {
	private static final Logger logger = Logger.getLogger(InheritanceInquiryMain.class);
	
	
	public static void main(String[] args) {
		try {
			new ProjectBuildService().build();
		} catch (TaskBuildException e) {
			logger.error(e);
		}
	}
	
}
