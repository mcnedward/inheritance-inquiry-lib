package com.mcnedward.ii;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InterfaceInquiryMain {

	private static final String PROJECT_NAME = "EatingCinci";
	private static final String PROJECT_PATH = "C:/users/edward/dev/workspace/eatingcinci-bak";
	
	public static void main(String[] args) {
		InterfaceInquiryEclipse interfaceInquiry = new InterfaceInquiryEclipse();
		interfaceInquiry.buildProject(PROJECT_PATH, PROJECT_NAME, new ProjectBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
			}

			@Override
			public void finished(JavaProject project) {
				System.out.println(project);
				System.out.println("Number of classes: " + project.getClasses().size());
				System.out.println("Number of interfaces: " + project.getInterfaces().size());
				
				Analyzer.calculateOverridenMethods(project);
			}

			@Override
			public void onBuildError(String message, Exception exception) {
			}
			
		});
	}

}
