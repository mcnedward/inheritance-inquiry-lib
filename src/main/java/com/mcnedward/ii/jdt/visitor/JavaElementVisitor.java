package com.mcnedward.ii.jdt.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaElementVisitor extends ProjectVisitor {
	protected static final Logger logger = Logger.getLogger(JavaElementVisitor.class);

	private JavaElement mElement;
	private String mElementName;
	private JavaClassOrInterfaceVisitor mClassOrInterfaceVisitor;
	private JavaMethodVisitor mMethodVisitor;
	
	public JavaElementVisitor(JavaProject project, String elementName) {
		super(project);
		mElementName = elementName;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(TypeDeclaration node) {
		try {
			element().setIsInterface(node.isInterface());
			
			// Setup all the interfaces
			List<ASTNode> interfaces = node.superInterfaceTypes();
			for (ASTNode inter : interfaces) {
				mClassOrInterfaceVisitor.reset();
				mClassOrInterfaceVisitor.setIsInterface(true);
				inter.accept(mClassOrInterfaceVisitor);
			}

			// Set the super class
			Type superClassType = node.getSuperclassType();
			if (superClassType != null) {
				mClassOrInterfaceVisitor.reset();
				mClassOrInterfaceVisitor.setIsInterface(node.isInterface());	// If this node is an interface, then it's "extends" will be as well
				superClassType.accept(mClassOrInterfaceVisitor);
			}
			
			for (Object declaration : node.bodyDeclarations()) {
				if (declaration instanceof MethodDeclaration) {
					((MethodDeclaration)declaration).accept(mMethodVisitor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().getFullyQualifiedName();
		mElement = project().findOrCreateElement(packageName, mElementName);
		mElement.setPackageName(packageName);

		mClassOrInterfaceVisitor = new JavaClassOrInterfaceVisitor(project(), mElement);
		mMethodVisitor = new JavaMethodVisitor(project(), mElement);
		
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		mElement.addImport(node.getName().getFullyQualifiedName());
		return super.visit(node);
	}
	
	public JavaElement element() {
		return mElement;
	}

	@Override
	public void preVisit(ASTNode node) {
		super.preVisit(node);
	}
	
	@Override
	public void postVisit(ASTNode node) {
		super.postVisit(node);
	}
}
