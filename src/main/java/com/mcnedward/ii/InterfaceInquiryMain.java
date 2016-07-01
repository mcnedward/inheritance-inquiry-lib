package com.mcnedward.ii;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InterfaceInquiryMain {

	private static final String PROJECT_LOCATION = "C:/Users/Edward/Dev/Workspace/eatingcinci-bak";
	private static final String GIT_REMOTE_URL = "https://github.com/mcnedward/program-analysis.git";
	
	public static void main(String[] args) {
		new InterfaceInquiry().buildProject(PROJECT_LOCATION, "eatingcinci", false, new ProjectBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
			}

			@Override
			public void finished(JavaProject project) {
//				new Analyzer().analyze(project);
				System.out.println("Finished");
				System.out.println("Number of classes: " + project.getClasses().size());
				System.out.println("Number of interfaces: " + project.getInterfaces().size());
			}
			
			@Override
			public void onBuildError(String message, Exception exception) {
				
			}
			
		});
	}
	
}
