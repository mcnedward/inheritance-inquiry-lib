package com.mcnedward.ii.jdt.visitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
 * @author Edward - Jun 16, 2016
 *
 */
public class ClassVisitor extends JavaProjectVisitor {
	protected static final Logger logger = Logger.getLogger(ClassVisitor.class);

	private JavaElement mElement;
	private String mPackageName;
	private String mElementName;
	private ClassOrInterfaceVisitor mClassOrInterfaceVisitor;
	private MethodVisitor mMethodVisitor;
	private TypeParameterVisitor mTypeParameterVisitor;
	private List<String> mImports;

	public ClassVisitor(JavaProject project, String elementName) {
		super(project);
		mElementName = elementName;
		mImports = new ArrayList<>();
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

			// Setup all the interfaces
			List<ASTNode> interfaces = node.superInterfaceTypes();
			for (ASTNode inter : interfaces) {
				mClassOrInterfaceVisitor.setIsInterface(true);
				inter.accept(mClassOrInterfaceVisitor);
			}

			// Set the super class
			Type superClassType = node.getSuperclassType();
			if (superClassType != null) {
				// If this node is an interface, then it's "extends" will be as well
				mClassOrInterfaceVisitor.setIsInterface(node.isInterface());
				superClassType.accept(mClassOrInterfaceVisitor);
			}

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
