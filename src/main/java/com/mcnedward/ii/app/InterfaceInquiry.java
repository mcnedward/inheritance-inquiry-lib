package com.mcnedward.ii.app;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.mcnedward.ii.app.element.JavaProject;
import com.mcnedward.ii.app.utils.Stopwatch;
import com.mcnedward.ii.app.visitor.JavaElementVisitor;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InterfaceInquiry {

	private Parser mParser;
	private ASTParser mAstParser;
	
	public InterfaceInquiry() {
		mParser = new Parser();
		mAstParser = ASTParser.newParser(AST.JLS3);
	}
	
	public JavaProject buildProject(String projectPath, String projectName) {
		JavaProject project = new JavaProject(projectPath, projectName);
		
		Stopwatch.start();
		try {
			// Get all the files for the project
			List<ParsedFile> files = mParser.parseDirectory(projectPath);
			
			boolean stillParsing = true;
			int numberOfFinishedFiles = 0;
			int totalNumberOfFiles = files.size();
			while (stillParsing) {
				for (ParsedFile file : files) {
					parse(file, project);
					numberOfFinishedFiles++;
				}
				if (numberOfFinishedFiles == totalNumberOfFiles) stillParsing = false;
			}
			
			String timeToComplete = Stopwatch.stopAndGetTime();
			System.out.println("FINISHED! Time to complete: " + timeToComplete);
		} catch (IOException e) {
			System.out.println("There was a problem when parsing files for the directory at: " + projectPath + ".");
			System.out.println(e);
		}
		
		return project;
	}
	
	public JavaProject buildFile(String filePath) {
		JavaProject project = new JavaProject(filePath, "Single file project");
		Stopwatch.start();
		
		try {
			ParsedFile file = mParser.parseFile(filePath);
			parse(file, project);
			String timeToComplete = Stopwatch.stopAndGetTime();
			System.out.println("FINISHED! Time to complete: " + timeToComplete);
		} catch (IOException e) {
			System.out.println("There was a problem when parsing file at: " + filePath + ".");
			System.out.println(e);
		}
		return project;
	}
	
	private void parse(ParsedFile file, JavaProject project) {
		mAstParser.setSource(file.getSource().toCharArray());
		mAstParser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) mAstParser.createAST(null);

		cu.accept(new JavaElementVisitor(project, file.getName()));
	}
	
}
