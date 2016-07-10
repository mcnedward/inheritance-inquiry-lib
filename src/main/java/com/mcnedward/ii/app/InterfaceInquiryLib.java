package com.mcnedward.ii.app;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InterfaceInquiryLib {

	private static final String DIRECTORY_PATH = "C:/users/edward/dev/workspace/eatingcinci-spring";

	private static Parser parser;

	public static void main(String[] args) {
		parser = new Parser();
		parseDirectory(DIRECTORY_PATH);
	}

	private static void parseDirectory(String directoryPath) {
		Stopwatch.start();
		try {
			List<String> filePromises = parser.parseDirectory(directoryPath);
			
			boolean stillParsing = true;
			int numberOfFinishedFiles = 0;
			int totalNumberOfFiles = filePromises.size();
			while (stillParsing) {
				for (String filePromise : filePromises) {
					System.out.println(String.format("PARSING2 %s/%s", ++numberOfFinishedFiles, totalNumberOfFiles));
					parse(filePromise);
				}
				if (numberOfFinishedFiles == totalNumberOfFiles) stillParsing = false;
			}
			
			String timeToComplete = Stopwatch.stopAndGetTime();
			System.out.println("FINISHED! Time to complete: " + timeToComplete);
		} catch (IOException e) {
			System.out.println("There was a problem when parsing files for the directory at: " + directoryPath + ".");
		}
	}

	private static void parse(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			Set<String> names = new HashSet<>();

			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				names.add(name.getIdentifier());
				return false;
			}

			public boolean visit(SimpleName node) {
				if (names.contains(node.getIdentifier())) {
				}
				return true;
			}
		});
	}

}
