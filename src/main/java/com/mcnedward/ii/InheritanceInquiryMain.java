package com.mcnedward.ii;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InheritanceInquiryMain {

	private static final String PROJECT_NAME = "EatingCinci";
	private static final String PROJECT_PATH = "C:/users/edward/dev/workspace/eatingcinci-bak";
	private static final String QUALITUS_CORPUS_SYSTEMS_PATH = "C:/QC/pt1/Systems/";
	
	public static void main(String[] args) {
		InheritanceInquiryEclipse interfaceInquiry = new InheritanceInquiryEclipse();
		interfaceInquiry.buildProject(QUALITUS_CORPUS_SYSTEMS_PATH + "azureus/azureus-2.0.8.2", PROJECT_NAME, new ProjectBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
			}

			@Override
			public void finished(JavaProject project) {
				System.out.println(project);
				System.out.println("Number of classes: " + project.getClasses().size());
				System.out.println("Number of interfaces: " + project.getInterfaces().size());
				System.out.println();
				
				Analyzer.analyze(project);
			}

			@Override
			public void onBuildError(String message, Exception exception) {
			}
			
		});
	}

}
