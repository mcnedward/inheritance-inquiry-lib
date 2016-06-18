package com.mcnedward.ii.app;

import com.mcnedward.ii.app.element.IJavaElement;
import com.mcnedward.ii.app.element.JavaProject;
import com.mcnedward.ii.app.listener.ProjectBuildListener;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InterfaceInquiryMain {

	private static final String PROJECT_NAME = "EatingCinci";
	private static final String PROJECT_PATH = "C:/users/edward/dev/workspace/eatingcinci-spring";
	@SuppressWarnings("unused")
	private static final String FILE_PATH = "C:/users/edward/dev/workspace/eatingcinci-spring/src/main/java/com/eatingcinci/app/entity/IEntity.java";
	
	public static void main(String[] args) {
		InterfaceInquiry interfaceInquiry = new InterfaceInquiry();
		interfaceInquiry.buildProject(PROJECT_PATH, PROJECT_NAME, new ProjectBuildListener() {
			
			@Override
			public void onProgressChange(String message, int progress) {
			}
			
			@Override
			public void finished(JavaProject project) {
				System.out.println(project);
				System.out.println("Number of classes: " + project.getClasses().size());
				System.out.println("Number of interfaces: " + project.getInterfaces().size());
				for (IJavaElement javaInterface : project.getInterfaces()) {
					System.out.println(javaInterface);
				}
			}
		});
	}

}
