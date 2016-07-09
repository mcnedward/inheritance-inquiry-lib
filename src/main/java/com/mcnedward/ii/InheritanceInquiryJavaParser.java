package com.mcnedward.ii;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jgit.util.FileUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaPackage;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.javaparser.visitor.ClassVisitor;
import com.mcnedward.ii.listener.GitDownloadListener;
import com.mcnedward.ii.listener.ProjectBuildListener;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InheritanceInquiryJavaParser {
	protected static final Logger logger = Logger.getLogger(InheritanceInquiryJavaParser.class);

	public InheritanceInquiryJavaParser() {
	}

	public void buildProject(String projectPath, String projectName, boolean deleteAfterBuild, ProjectBuildListener listener) {
		Runnable task = () -> {
			listener.onProgressChange(String.format("Starting to load %s...", projectName), 0);
			JavaProject project = new JavaProject(projectPath, projectName);
			build(project, deleteAfterBuild, listener);
			if (project != null)
				listener.finished(project);
		};

		Thread thread = new Thread(task);
		thread.start();
	}

	public void buildProject(File projectFile, String projectName, boolean deleteAfterBuild, ProjectBuildListener listener) {
		Runnable task = () -> {
			listener.onProgressChange(String.format("Starting to load %s...", projectName), 0);
			JavaProject project = new JavaProject(projectFile, projectName);
			build(project, deleteAfterBuild, listener);
			if (project != null)
				listener.finished(project);
		};

		Thread thread = new Thread(task);
		thread.start();
	}

	public void buildProject(String remoteUrl, String username, String password, ProjectBuildListener listener) {
		GitService.downloadFileFromGit(remoteUrl, username, password, new GitDownloadListener() {

			@Override
			public void onProgressChange(String message, int progress) {
			}

			@Override
			public void onDownloadError(String message, Exception exception) {
			}

			@Override
			public void finished(File gitFile, String repoName) {
				buildProject(gitFile, repoName, true, listener);
			}

		});
	}


	/**
	 * Build the JavaProject by parsing every java file and visiting the required nodes.
	 * 
	 * @param project
	 *            The JavaProject to build.
	 * @param listener
	 *            Listener for notifying of changes.
	 * @param deleteAfterBuild
	 *            True if the JavaProject file should be deleted after the build, such as when the file is a temporary
	 *            file from a git download.
	 * @return The built JavaProject, or null if the project failed to build.
	 */
	private JavaProject build(JavaProject project, boolean deleteAfterBuild, ProjectBuildListener listener) {
		try {
			ClassVisitor mClassVisitor = new ClassVisitor(project);

			List<CompilationHolder> compilationHolders = parseProject(project, listener);
			listener.onProgressChange("File loaded.", 0);

			int classesCount = compilationHolders.size();
			for (int i = 0; i < compilationHolders.size(); i++) {
				CompilationHolder holder = compilationHolders.get(i);

				int progress = (int) (((double) i / classesCount) * 100);
				listener.onProgressChange(String.format("Analyzing..."), progress);

				JavaElement element = new JavaElement();
				mClassVisitor.visit(holder.compilationUnit, element);
			}
			// Check every element for ClassOrInterfaces
			updateElementsAfterBuild(project);

			// TODO Update progress correctly
			listener.onProgressChange(String.format("Finished!"), 100);

			return project;
		} catch (IOException | ParseException e) {
			listener.onProgressChange("Something went wrong loading the file.", 100);
			logger.error("Something went wrong loading the file...", e);
			return null;
		} finally {
			if (deleteAfterBuild) {
				try {
					FileUtils.delete(project.getProjectFile(), FileUtils.RECURSIVE | FileUtils.RETRY);
					logger.info(String.format("Deleting %s", project.getProjectFile().getName()));
				} catch (IOException e) {
					try {
						logger.warn(String.format("Could not delete file: %s... Trying one more time.", project.getProjectFile()));
						FileUtils.delete(project.getProjectFile(), FileUtils.RECURSIVE | FileUtils.RETRY);
					} catch (IOException e1) {
						logger.error(String.format("Could not delete file: %s after second attempt...", project.getProjectFile()), e);
					}
				}
			}
		}
	}

	/**
	 * Checks all JavaElements in the project for any ClassOrInterfaces, then gives them the correct JavaElement
	 * corresponding to that ClassOrInterface.
	 * 
	 * @param project
	 *            The JavaProject
	 */
	private void updateElementsAfterBuild(JavaProject project) {
		for (JavaPackage javaPackage : project.getPackages()) {
			Set<JavaElement> javaElements = javaPackage.getElements();
			for (JavaElement element : javaElements) {
				if (element.needsInterfaceStatusChecked()) {
					// Needs to be checked, so find all the classes or interfaces used by this element
					List<JavaElement> elementsToCheck = element.getElements();
					for (JavaElement elementToCheck : elementsToCheck) {
						elementToCheck = project.find(elementToCheck.getName());
					}
				}
				if (element.needsMissingTypeArgChecked()) {
					for (String typeArg : element.getMissingTypeArgs()) {
						JavaElement missingElement = project.find(typeArg);
						if (missingElement == null) {
							logger.debug(String.format("Still could not find the type argument element %s in the JavaElement %s", typeArg, element));
						} else {
							logger.debug(String.format("Found missing element %s.", missingElement));
							element.addTypeArg(missingElement);
						}
					}
				}
				if (element.needsMissingClassOrInterfaceChecked()) {
					for (String coi : element.getMissingClassOrInterfaceList()) {
						JavaElement missingElement = project.find(coi);
						if (missingElement == null) {
							logger.debug(String.format("Still could not find the class or interface element %s in the JavaElement %s", coi, element));
						} else {
							logger.debug(String.format("Found missing element %s.", missingElement));
							element.addElement(missingElement);
						}
					}
				}
				element.cleanUp();
			}
		}
	}

	/**
	 * Loads the file or directory and creates the CompilationUnits.
	 * 
	 * @param selectedFile
	 *            The selected file or directory.
	 * @throws IOException
	 * @throws ParseException
	 */
	private List<CompilationHolder> parseProject(JavaProject project, ProjectBuildListener listener) throws IOException, ParseException {
		logger.info("Loading: " + project.getPath());
		List<CompilationHolder> compilationHolders = new ArrayList<>();
		List<File> projectFiles = project.getFiles();
		int fileCount = projectFiles.size();
		for (int i = 0; i < fileCount; i++) {
			File file = projectFiles.get(i);

			int progress = (int) (((double) i / fileCount) * 100);
			listener.onProgressChange(String.format("Parsing..."), progress);

			CompilationUnit cu = JavaParser.parse(file);
			compilationHolders.add(new CompilationHolder(cu, file));
		}
		return compilationHolders;
	}

}

class CompilationHolder {
	protected CompilationUnit compilationUnit;
	protected File file;

	protected CompilationHolder(CompilationUnit cu, File file) {
		compilationUnit = cu;
		this.file = file;
	}
}
