package com.mcnedward.ii;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.listener.ProjectBuildListener;

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
				JavaElement element = project.find("AccountRepositoryImpl");
				recurse(element);
			}
		});
	}
	
	private static void recurse(JavaElement element) {
		for (JavaElement javaInterface : element.getInterfaces()) {
			System.out.println(String.format("%s %s %s",
					element,
					(element.isInterface() || (!element.isInterface() && !javaInterface.isInterface()) ? "extends" : "implements"),
					javaInterface));
			if (!javaInterface.getInterfaces().isEmpty()) {
				recurse(javaInterface);
			}
		}
	}

}
