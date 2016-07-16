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
public class ProjectBuildTask implements Callable<JavaProject> {

	private ProjectBuilder mBuilder;
	private JavaSystem mSystem;
	private File mProjectFile;
	
	public ProjectBuildTask(ProjectBuilder builder, JavaSystem system, File projectFile) {
		mBuilder = builder;
		mSystem = system;
		mProjectFile = projectFile;
	}

	@Override
	public JavaProject call() throws Exception {
		JavaProject project = mBuilder.build(mProjectFile, mSystem.getName());
		return project;
	}

}
