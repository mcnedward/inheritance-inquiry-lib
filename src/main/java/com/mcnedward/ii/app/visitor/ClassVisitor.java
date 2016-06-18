package com.mcnedward.ii.app.visitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.mcnedward.ii.app.element.JavaElement;
import com.mcnedward.ii.app.element.JavaPackage;
import com.mcnedward.ii.app.element.JavaProject;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public class ClassVisitor extends BaseVisitor<JavaElement> {
	protected static final Logger logger = Logger.getLogger(ClassVisitor.class);

	private JavaProject mProject;
	private String mPackageName;
	private List<String> mImports;
	private JavaElement mElement;

	public ClassVisitor(JavaProject project) {
		super();
		mProject = project;
		mImports = new ArrayList<>();
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration node, JavaElement element) {
		String elementName = node.getName();
		boolean isInterface = node.isInterface();
		// First try and find this class or interface in the JavaProject
		mElement = findOrCreateElement(mPackageName, elementName);

		mElement.setIsInterface(isInterface);
		mElement.setNeedsInterfaceStatusChecked(false); // Doesn't need to be checked after build anymore
		mElement.setName(node.getName());

		checkExtends(node);

		// if (n.getImplements() != null && !n.getImplements().isEmpty())
		// for (ClassOrInterfaceType coi : n.getImplements())
		// classObject.addInterface(coi.getName());
		//
		// for (BodyDeclaration member : n.getMembers()) {
		// if (member instanceof FieldDeclaration) {
		// variableVisitor.visit((FieldDeclaration) member, null);
		// }
		// if (member instanceof MethodDeclaration) {
		// methodVisitor.visit((MethodDeclaration) member, null);
		// }
		// }
	}

	@Override
	public void visit(PackageDeclaration node, JavaElement element) {
		mPackageName = node.getPackageName();
	}

	@Override
	public void visit(ImportDeclaration node, JavaElement element) {
		mImports.add(node.getName().toString());
	}

	private void checkExtends(ClassOrInterfaceDeclaration node) {
		if (node.getExtends() != null && !node.getExtends().isEmpty()) {
			for (ClassOrInterfaceType coi : node.getExtends()) {
				String name = coi.getName();
				String packageName = null;
				// Get the package name from the imports
				for (String importName : mImports) {
					int index = importName.lastIndexOf('.');
					if (index > 0) {
						String imp = importName.substring(index + 1);
						if (name.equals(imp)) {
							packageName = importName.substring(0, importName.indexOf(name) - 1);
							break;
						}
					}
				}
				if (packageName == null) {
					// Search the current package for this class or interface
					JavaPackage currentPackage = mProject.findPackage(mPackageName);
					JavaElement elementInPackage = currentPackage.find(name); // currentPackage should never be null
					if (elementInPackage != null) {
						// TODO Found, so the element must be in the same package?
						mElement.addElement(elementInPackage);
					} else {
						logger.error(String.format("Could not find the package for %s in the JavaElement %s.", name, mElement));
						mElement.setNeedsMissingClassOrInterfaceChecked(true);
						mElement.addMissingClassOrInterface(name);
					}
				} else {
					// Search the project for an existing package
					JavaElement element = findOrCreateElement(packageName, name);
					mElement.addElement(element);
				}
			}
		}
	}

	private JavaElement findOrCreateElement(String packageName, String elementName) {
		JavaElement element = null;
		JavaPackage javaPackage = mProject.findPackage(packageName);
		if (javaPackage == null) {
			// Package does not exist, so class cannot either.
			// Add the class to the package, and the package to the project
			javaPackage = new JavaPackage(packageName);
			element = new JavaElement(elementName);
			javaPackage.addElement(element);
			mProject.addPackage(javaPackage);
			element.setNeedsInterfaceStatusChecked(true);
		} else {
			// Find the class in the package
			element = javaPackage.find(elementName);
			if (element == null) {
				element = new JavaElement(elementName);
				javaPackage.addElement(element);
				element.setNeedsInterfaceStatusChecked(true);
			}
		}
		return element;
	}

	public JavaElement getElement() {
		return mElement;
	}

	public void reset() {
		mImports = new ArrayList<>();
	}

}
