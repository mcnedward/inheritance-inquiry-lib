package com.mcnedward.ii;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jgit.util.FileUtils;

import com.mcnedward.ii.element.ClassOrInterfaceHolder;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaPackage;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.jdt.visitor.JavaElementVisitor;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.utils.ASTUtils;
import com.mcnedward.ii.utils.Stopwatch;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InterfaceInquiryEclipse extends FileASTRequestor {
	private static final Logger logger = Logger.getLogger(InterfaceInquiryEclipse.class);

	private Sourcer mSourcer;
	private List<CompilationUnitHolder> mHolders;

	public InterfaceInquiryEclipse() {
		mSourcer = new Sourcer();
		mHolders = new ArrayList<>();
	}

	public void buildProject(String projectPath, String projectName, ProjectBuildListener listener) {
		Runnable task = () -> {
			listener.onProgressChange(String.format("Starting to load %s...", projectName), 0);
			JavaProject project = new JavaProject(projectPath, projectName);
			build(project, false, listener);
			if (project != null)
				listener.finished(project);
		};

		Thread thread = new Thread(task);
		thread.start();
	}

	private JavaProject build(JavaProject project, boolean deleteAfterBuild, ProjectBuildListener listener) {
		Stopwatch.start();
		try {
			// Get all the files for the project
			List<SourcedFile> files = mSourcer.buildSourceForProject(project, listener);

			@SuppressWarnings("unchecked")
			Hashtable<String, String> options = JavaCore.getOptions();
			options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
			options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
			
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setCompilerOptions(options);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);
			
			String[] classPaths = ASTUtils.classPathEntries();
			String[] sources = ASTUtils.sourceEntries(project.getProjectFile().getAbsolutePath());
			String[] encodings = ASTUtils.encodings();
			parser.setEnvironment(classPaths, sources, encodings, true);

			List<String> filePaths = new ArrayList<>();
			for (SourcedFile file : files) {
				filePaths.add(file.getFile().getAbsolutePath());
			}
			String[] sourceFiles = filePaths.toArray(new String[filePaths.size()]); 
			parser.createASTs(sourceFiles, null, new String[0], this, null);
			
			for (int i = 0; i < mHolders.size(); i++) {
				CompilationUnitHolder holder = mHolders.get(i);
				
				int progress = (int) (((double) i / mHolders.size()) * 100);
				listener.onProgressChange("Analyzing...", progress);
				
				String fileName = holder.sourceFilePath;
				for (SourcedFile f : files) {
					if (f.getFile().getAbsolutePath().equals(holder.sourceFilePath)) {
						fileName = f.getName();
						break;
					}
				}
				CompilationUnit cu = holder.cu;
				
				IProblem[] problems = cu.getProblems();
				if (problems != null && problems.length > 0) {
			        logger.warn(String.format("Got %s problems compiling the source file: ", problems.length));
			        for (IProblem problem : problems) {
			            logger.warn(String.format("%s", problem));
			        }
			    }
				
				cu.accept(new JavaElementVisitor(project, fileName));
			}
			
			// Check every element for ClassOrInterfaces
			updateElementsAfterBuild(project);

			String timeToComplete = Stopwatch.stopAndGetTime();
			System.out.println("FINISHED! Time to complete: " + timeToComplete);
			listener.onProgressChange(String.format("Finished! Time to complete: %s", timeToComplete), 100);
			return project;
		} catch (IOException e) {
			listener.onProgressChange(String.format("Something went wrong loading the file %s.", project.getProjectFile()), 100);
			logger.error(String.format("Something went wrong loading the file %s.", project.getProjectFile()), e);
			return null;
		} finally {
			if (deleteAfterBuild) {
				try {
					FileUtils.delete(project.getProjectFile(), FileUtils.RECURSIVE | FileUtils.RETRY);
					logger.info(String.format("Deleting %s", project.getProjectFile().getName()));
				} catch (IOException e) {
					try {
						FileUtils.delete(project.getProjectFile(), FileUtils.RECURSIVE | FileUtils.RETRY);
						logger.warn(String.format("Could not delete file: %s... Trying one more time.", project.getProjectFile()));
					} catch (IOException e1) {
						logger.error(String.format("Could not delete file: %s after second attempt...", project.getProjectFile()), e);
					}
				}
			}
		}
	}
	
	@Override
	public void acceptAST(String sourceFilePath, CompilationUnit compilationUnit) {
		CompilationUnitHolder holder = new CompilationUnitHolder();
		holder.cu = compilationUnit;
		holder.sourceFilePath = sourceFilePath;
		mHolders.add(holder);
	}
	
	/**
	 * Checks all JavaElements in the project for any ClassOrInterfaces, then gives them the correct JavaElement
	 * corresponding to that ClassOrInterface.
	 * 
	 * @param project
	 *            The JavaProject
	 */
	private void updateElementsAfterBuild(JavaProject project) {
		for (JavaPackage javaPackage : new ArrayList<>(project.getPackages())) {
			Set<JavaElement> javaElements = javaPackage.getElements();
			for (JavaElement element : javaElements) {
				if (element.needsInterfaceStatusChecked()) {
					// Needs to be checked, so find all the classes or interfaces used by this element
					List<JavaElement> elementsToCheck = element.getElements();
					for (JavaElement elementToCheck : elementsToCheck) {
						elementToCheck = project.find(elementToCheck.getName());
					}
				}
				if (element.getHolders().isEmpty())
					continue;
				for (ClassOrInterfaceHolder holder : element.getHolders()) {
					String coiName = holder.getName();
					JavaElement coi = project.find(coiName);
					if (coi == null) {
						String coiPackage = checkImportsForPackage(coiName, element.getImports());
						if (coiPackage != null) {
							coi = project.findOrCreateElement(coiPackage, coiName);
							coi.setIsInterface(holder.isInterface());
						}
						if (coi == null) {
							logger.debug(String.format("Could not find element named: \"%s\" for element: %s.", coiName, element));
							continue;
						}
					}
					element.addElement(coi);
					for (String typeArgName : holder.getTypeArgs()) {
						JavaElement typeArg = project.find(typeArgName);
						if (typeArg != null) {
							coi.addTypeArg(typeArg);
						}
					}
				}
			}
		}
	}

	/**
	 * Searches the imports for an element that has been imported.
	 * 
	 * @param elementName
	 *            The name of the element to find the package for.
	 * @param imports
	 *            The list of imports from the JavaElement passed in to the visit() method.
	 * @return The name of the package for the element, if found. Null if the package name is not found.
	 */
	private String checkImportsForPackage(String elementName, List<String> imports) {
		String packageName = null;
		// Get the package name from the imports
		for (String importName : imports) {
			int index = importName.lastIndexOf('.');
			if (index > 0) {
				String imp = importName.substring(index + 1);
				if (elementName.equals(imp)) {
					packageName = importName.substring(0, importName.indexOf(elementName) - 1);
					break;
				}
			}
		}
		return packageName;
	}

	private final class CompilationUnitHolder {
		public CompilationUnit cu;
		public String sourceFilePath;
	}
	
}
