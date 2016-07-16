package com.mcnedward.ii.tasks;

import java.io.File;
import java.util.concurrent.Callable;

import com.mcnedward.ii.builder.ProjectBuilder;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public class ProjectBuildCall implements Callable<JavaProject> {

	private ProjectBuilder mBuilder;
	private JavaSystem mSystem;
	private File mProjectFile;
	
	public ProjectBuildCall(JavaSystem system, File projectFile) {
		mSystem = system;
		mProjectFile = projectFile;
		mBuilder = new ProjectBuilder();
	}

	@Override
	public JavaProject call() throws Exception {
		JavaProject project = mBuilder.build(mProjectFile, mSystem.getName());
		return project;
	}

}
