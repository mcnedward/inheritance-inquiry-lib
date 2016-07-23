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
		try {
			mClassOrInterfaceVisitor = new ClassOrInterfaceVisitor(project(), element());
			mMethodVisitor = new MethodVisitor(project(), element());
			mTypeParameterVisitor = new TypeParameterVisitor(project(), element());
			
			element().setImports(mImports);
			element().setIsInterface(node.isInterface());
			
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
				mClassOrInterfaceVisitor.setIsInterface(node.isInterface());	// If this node is an interface, then it's "extends" will be as well
				superClassType.accept(mClassOrInterfaceVisitor);
			}
			
			for (Object declaration : node.bodyDeclarations()) {
				if (declaration instanceof MethodDeclaration) {
					((MethodDeclaration)declaration).accept(mMethodVisitor);
				}
			}
		} catch (Exception e) {
			IILogger.error("Exception in ClassVisitor when visiting node: " + node.getName() + " in project: " + project().getName(), e);
		}
		return false;
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().getFullyQualifiedName();
		mElement = project().findOrCreateElement(packageName, mElementName);
		mElement.setPackageName(packageName);		
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		mImports.add(node.getName().getFullyQualifiedName());
		return super.visit(node);
	}

	public JavaElement element() {
		if (mElement == null) {
			mElement = project().findOrCreateElement(null, mElementName);
		}
		return mElement;
	}
}
