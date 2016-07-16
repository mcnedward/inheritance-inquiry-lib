package com.mcnedward.ii.tasks;

import java.io.File;
import java.util.concurrent.Callable;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.service.ProjectService;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public class ProjectBuildCall implements Callable<JavaProject> {

	private ProjectService mBuilder;
	private JavaSystem mSystem;
	private File mProjectFile;
	
	public ProjectBuildCall(JavaSystem system, File projectFile) {
		mSystem = system;
		mProjectFile = projectFile;
		mBuilder = new ProjectService();
	}

	@Override
	public JavaProject call() throws Exception {
		JavaProject project = mBuilder.build(mProjectFile, mSystem.getName());
		return project;
	}

}
