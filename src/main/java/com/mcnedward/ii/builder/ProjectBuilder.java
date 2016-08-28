package com.mcnedward.ii.builder;

import java.io.File;

import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.exception.TaskBuildException;
import com.mcnedward.ii.tasks.StandardBuildTask;

/**
 * A tool for building {@JavaProject}s and {@link JavaSystem}s.
 * 
 * @author Edward - Jul 14, 2016
 *
 */
public final class ProjectBuilder extends Builder {

	protected static final String PROJECT_NAME = "argouml";
	protected static final String PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "argouml/argouml-0.34";
	protected static final String FREECOL_PROJECT_NAME = "freecol";
	protected static final String FREECOL_PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "freecol/freecol-0.9.5 ";
	protected static final String HIBERNATE_PROJECT_NAME = "hibernate";
	protected static final String HIBERNATE_PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "hibernate/hibernate-0.8.1";
	protected static final String JHOTDRAW_PROJECT_NAME = "jhotdraw";
	protected static final String JHOTDRAW_PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "jhotdraw/jhotdraw-6.0.1";
	protected static final String ANT_PROJECT_NAME = "ant";
	protected static final String ANT_PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "ant/ant-1.8.4";
	protected static final String ANTLR_PROJECT_NAME = "antlr";
	protected static final String ANTLR_PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "antlr/antlr-4.0";
	
	public ProjectBuilder() {
		super();
	}
	
	@Override
	protected void buildProcess() throws TaskBuildException {
		String projectPath = FREECOL_PROJECT_PATH; 
		String projectName = FREECOL_PROJECT_NAME;
		File buildFile = new File(projectPath);
		if (!buildFile.exists()) {
			throw new TaskBuildException(String.format("You need to provide an existing file! [Path: %s]", buildFile.getAbsolutePath()));
		}
		
		submit(new StandardBuildTask(buildFile, projectName));

		waitForTasks();
	}

}
