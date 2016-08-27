package com.mcnedward.ii.jdt.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.utils.IILogger;

/**
 * A Visitor for inspecting a Java type, creating a JavaElement, and adding it to the correct JavaPackage.
 * 
 * @author Edward - Jun 16, 2016
 *
 */
public class ClassVisitor extends JavaProjectVisitor {

	private JavaElement mElement;
	private String mPackageName;
	private String mElementName;
	private ClassOrInterfaceVisitor mClassOrInterfaceVisitor;
	private MethodVisitor mMethodVisitor;
	private TypeParameterVisitor mTypeParameterVisitor;
	private List<String> mImports;
	private boolean mIncludeInterfaces;

	public ClassVisitor(JavaProject project, String elementName, boolean includeInterfaces) {
		super(project);
		mElementName = elementName;
		mImports = new ArrayList<>();
		mIncludeInterfaces = includeInterfaces;
	}

	public ClassVisitor(JavaProject project, String elementName) {
		this(project, elementName, false);	// Don't include interfaces; this should be the default choice
	}

	public ClassVisitor(JavaProject project, String elementName, List<String> missingImports) {
		this(project, elementName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(TypeDeclaration node) {
		String className = node.getName().toString();

		// If the name of this node is different than the name passed into the visitor, then this node is a
		// TypeDeclaration for another class declared in the same file
		JavaElement element;
		if (className.equals(mElementName)) {
			element = element();
		} else {
			element = element(className);
		}

		try {
			mClassOrInterfaceVisitor = new ClassOrInterfaceVisitor(project(), element);
			mMethodVisitor = new MethodVisitor(project(), element());
			mTypeParameterVisitor = new TypeParameterVisitor(project(), element);

			element.setImports(mImports);
			element.setIsInterface(node.isInterface());

			List<TypeParameter> typeParameters = node.typeParameters();
			for (TypeParameter typeParameter : typeParameters) {
				typeParameter.accept(mTypeParameterVisitor);
			}

			if (mIncludeInterfaces) {
				// Visit all the interfaces
				List<ASTNode> interfaces = node.superInterfaceTypes();
				for (ASTNode inter : interfaces) {
					mClassOrInterfaceVisitor.setIsInterface(true);
					inter.accept(mClassOrInterfaceVisitor);
				}
			}

			// Visit the super class
			Type superClassType = node.getSuperclassType();
			if (superClassType != null) {
				// If this node is an interface, then it's "extends" will be as well
				mClassOrInterfaceVisitor.setIsInterface(node.isInterface());
				superClassType.accept(mClassOrInterfaceVisitor);
				// If this is a class, increase the inheritance count
				if (!node.isInterface())
					project().incrementInheritanceUse();
			}

			// Visit the methods
			for (Object declaration : node.bodyDeclarations()) {
				if (declaration instanceof MethodDeclaration) {
					((MethodDeclaration) declaration).accept(mMethodVisitor);
				}
			}
		} catch (Exception e) {
			IILogger.error("Exception in ClassVisitor when visiting node: " + node.getName() + " in project: " + project().getName(), e);
		}
		return false;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		mPackageName = node.getName().getFullyQualifiedName();

		mElement = project().findOrCreateElement(mPackageName, mElementName);
		mElement.setPackageName(mPackageName);
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		mImports.add(node.getName().getFullyQualifiedName());
		return super.visit(node);
	}

	public JavaElement element() {
		if (mElement == null) {
			mElement = project().findOrCreateElement(mPackageName, mElementName);
		}
		return mElement;
	}

	/**
	 * To use when getting an class that is declared in the same file as another class.
	 * 
	 * @param name
	 *            The name of the class
	 * @return The JavaElement
	 */
	public JavaElement element(String name) {
		return project().findOrCreateElement(mPackageName, name);
	}
}
