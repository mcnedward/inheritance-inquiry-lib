package com.mcnedward.ii.jdt.visitor;

import java.util.List;

import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * This is the Visitor to use when visiting nodes inside of a {@link JavaElement}. This will contain a reference to the
 * parent JavaElement, so you can add new items to that element by using the element() method.
 * 
 * @author Edward - Jul 8, 2016
 */
public abstract class JavaElementVisitor extends JavaProjectVisitor {

	private JavaElement mParentElement;

	public JavaElementVisitor(JavaProject project, JavaElement parentElement) {
		super(project);
		mParentElement = parentElement;
	}

	protected JavaElement parentElement() {
		return mParentElement;
	}

	protected JavaElement findOrCreateElement(String name, ITypeBinding binding) {
		String packageName = checkImportsForPackage(name, parentElement().getImports());
		if (packageName == null) {
			if (binding != null) {
				IPackageBinding packageBinding = binding.getPackage();
				if (packageBinding != null) {
					packageName = packageBinding.getName();
				}
			}
		}
		if (packageName == null) {
			packageName = "default";
		}

		return project().findOrCreateElement(packageName, name);
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
	protected String checkImportsForPackage(String elementName, List<String> imports) {
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
