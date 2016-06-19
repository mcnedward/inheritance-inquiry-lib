package com.mcnedward.ii.app.visitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
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

		// If this class is an interface, then any extends will also have to be interfaces.
		checkClassesOrInterfaces(node.getExtends(), isInterface);
		// Implements are always interfaces
		checkClassesOrInterfaces(node.getImplements(), true);
	}

	@Override
	public void visit(PackageDeclaration node, JavaElement element) {
		mPackageName = node.getPackageName();
	}

	@Override
	public void visit(ImportDeclaration node, JavaElement element) {
		mImports.add(node.getName().toString());
	}

	private void checkClassesOrInterfaces(List<ClassOrInterfaceType> list, boolean isInterface) {
		for (ClassOrInterfaceType coi : list) {
			String name = coi.getName();
			JavaElement coiElement = checkImportsForElement(name, mElement, true);
			if (coiElement != null)
				coiElement.setIsInterface(isInterface);

			List<Type> typeArgs = coi.getTypeArgs();
			for (Type typeArg : typeArgs) {
				String typeName = typeArg.toString();
				checkImportsForElement(typeName, coiElement, false);
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
			element = new JavaElement(elementName, javaPackage);
			javaPackage.addElement(element);
			mProject.addPackage(javaPackage);
			element.setNeedsInterfaceStatusChecked(true);
		} else {
			// Find the class in the package
			element = javaPackage.find(elementName);
			if (element == null) {
				element = new JavaElement(elementName, javaPackage);
				javaPackage.addElement(element);
				element.setNeedsInterfaceStatusChecked(true);
			}
		}
		return element;
	}

	/**
	 * <p>
	 * This method is used to check the imports for the current class (the one being visited here) and try to find the
	 * package for the specified elementName. This elementName is the name of a JavaElement that needs to be added to
	 * the elementToUpdate, and can be the name of a class or interface, or the name of a type argument in a
	 * parameterized generic declaration.
	 * </p>
	 * <p>
	 * The elementToUpdate is the element that will have the found JavaElement added to it. This elementToUpdate can be
	 * the current class, or a class or interface with parameterized types.
	 * </p>
	 * <p>
	 * This also handles setting the elementToUpdate to need checking after the build, if the correct element could not
	 * be found for certain.
	 * </p>
	 * 
	 * @param elementName
	 *            The name of the element to find.
	 * @param elementToUpdate
	 *            The JavaElement to add the found element to.
	 * @param classOrInterface
	 *            True if this is being checked for a class or interface, false if it is being checked for a type
	 *            argument.
	 * 
	 * @return The found JavaElement.
	 */
	private JavaElement checkImportsForElement(String elementName, JavaElement elementToUpdate, boolean classOrInterface) {
		String packageName = checkImportsForPackage(elementName);
		JavaElement foundElement;
		if (packageName == null) {
			// Search the current package for this class or interface
			JavaPackage currentPackage = mProject.findPackage(mPackageName);
			foundElement = currentPackage.find(elementName); // currentPackage should never be null
			if (foundElement == null) {
				logger.error(String.format("Could not find the package for the %s %s in the JavaElement %s.",
						(classOrInterface ? "class or interface" : "type argument"), elementName, elementToUpdate));
				if (classOrInterface) {
					elementToUpdate.setNeedsMissingClassOrInterfaceChecked(true);
					elementToUpdate.addMissingClassOrInterface(elementName);
				} else {
					elementToUpdate.setNeedsMissingTypeArgChecked(true);
					elementToUpdate.addMissingTypeArg(elementName);
				}
				return null;
				// TODO throw JavaElementNotFoundException?
			}
		} else {
			// Search the project for an existing package
			foundElement = findOrCreateElement(packageName, elementName);
		}
		if (classOrInterface)
			elementToUpdate.addElement(foundElement);
		else
			elementToUpdate.addTypeArg(foundElement);
		return foundElement;
	}

	private String checkImportsForPackage(String elementName) {
		String packageName = null;
		// Get the package name from the imports
		for (String importName : mImports) {
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

	public JavaElement getElement() {
		return mElement;
	}

	public void reset() {
		mImports = new ArrayList<>();
	}

}
