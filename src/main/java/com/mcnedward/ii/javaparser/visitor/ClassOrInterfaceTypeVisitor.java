package com.mcnedward.ii.javaparser.visitor;

import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaPackage;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 19, 2016
 *
 */
public class ClassOrInterfaceTypeVisitor extends BaseVisitor<JavaElement> {
	protected static final Logger logger = Logger.getLogger(ClassOrInterfaceTypeVisitor.class);

	private boolean mIsInterface;
	
	public ClassOrInterfaceTypeVisitor(JavaProject project) {
		super(project);
	}
	
	/**
	 * Used to determine whether the class or interface represented by this visited node is an interface or not.
	 * If the JavaElement passed in to the visit() method is an interface, then any extends will also have to be interfaces.
	 * If the JavaElement passed in to the visit() method is not an interface, then any extends will have to be classes.
	 * @param isInterface
	 */
	public void setIsInterface(boolean isInterface) {
		mIsInterface = isInterface;
	}

	@Override
	public void visit(ClassOrInterfaceType node, JavaElement element) {
		String name = node.getName();
		String packageName = checkImportsForPackage(name, element.getImports());
		JavaElement coiElement = checkImportsForElement(name, packageName, element.getPackageName(), element, true);
		if (coiElement != null) {
			coiElement.setIsInterface(mIsInterface);
			
			List<Type> typeArgs = node.getTypeArgs();
			for (Type typeArg : typeArgs) {
				String typeName = typeArg.toString();
				String typePackageName = checkImportsForPackage(typeName, element.getImports());
				checkImportsForElement(typeName, typePackageName, element.getPackageName(), coiElement, false);
			}
		}
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
	 * @param elementPackageName
	 *            The name of the package of the element to find. If this is set, then the element can be searched in
	 *            the project using that package.
	 * @param currentPackage
	 *            The name of the current package, from the JavaElement passed in to the visit() method. This will be
	 *            used to search the project is the elementPackageName is not set.
	 * @param elementToUpdate
	 *            The JavaElement to add the found element to.
	 * @param classOrInterface
	 *            True if this is being checked for a class or interface, false if it is being checked for a type
	 *            argument.
	 * 
	 * @return The found JavaElement.
	 */
	private JavaElement checkImportsForElement(String elementName, String elementPackageName, String currentPackageName, JavaElement elementToUpdate,
			boolean classOrInterface) {
		JavaElement foundElement;
		if (elementPackageName == null || elementPackageName.equals("")) {
			// Search the current package for this class or interface
			JavaPackage currentPackage = project().findPackage(currentPackageName);
			foundElement = currentPackage.find(elementName); // currentPackage should never be null
			if (foundElement == null) {
				logger.debug(String.format("Could not find the package for the %s %s in the JavaElement %s.",
						(classOrInterface ? "class or interface" : "type argument"), elementName, elementToUpdate));
				if (classOrInterface) {
					elementToUpdate.addMissingClassOrInterface(elementName);	// TODO add package to the element name?
				} else {
					elementToUpdate.addMissingTypeArg(elementName);
				}
				return null;
				// TODO throw JavaElementNotFoundException?
			}
		} else {
			// Search the project for an existing package
			foundElement = project().findOrCreateElement(elementPackageName, elementName);
		}
		if (classOrInterface)
			elementToUpdate.addElement(foundElement);
		else
			elementToUpdate.addTypeArg(foundElement);
		return foundElement;
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

}
