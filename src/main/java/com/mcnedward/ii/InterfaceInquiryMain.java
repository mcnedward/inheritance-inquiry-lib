package com.mcnedward.ii;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InterfaceInquiryMain {

	private static final String LOCAL_PROJECT_NAME = "EatingCinci";
	private static final String LOCAL_PROJECT_PATH = "C:/users/edward/dev/workspace/eatingcinci-spring";
	private static final String GIT_REMOTE_URL = "https://github.com/mcnedward/program-analysis.git";
	
	public static void main(String[] args) {
		new InterfaceInquiry().buildProject(GIT_REMOTE_URL, args[0], args[1], new ProjectBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
			}

			@Override
			public void finished(JavaProject project) {
//				new Analyzer().analyze(project);
			}
			
			@Override
			public void onBuildError(String message, Exception exception) {
				
			}
			
		});
	}
	
}
