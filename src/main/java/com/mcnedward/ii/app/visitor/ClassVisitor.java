package com.mcnedward.ii.app.visitor;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.mcnedward.ii.app.element.IJavaElement;
import com.mcnedward.ii.app.element.JavaElement;
import com.mcnedward.ii.app.element.JavaProject;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public class ClassVisitor extends BaseVisitor<IJavaElement> {
	protected static final Logger logger = Logger.getLogger(ClassVisitor.class);

	private JavaProject mProject;
	private IJavaElement mElement;
	
	public ClassVisitor(JavaProject project) {
		super();
		mProject = project;
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration node, IJavaElement element) {
		String elementName = node.getName();
		boolean isInterface = node.isInterface();
		// First try and find this class or interface in the JavaProject
		mElement = mProject.find(elementName);
		if (mElement == null) {
			mElement = new JavaElement(elementName, isInterface);
			mProject.addElement(mElement);
		} else {
			mElement.setIsInterface(isInterface);	// Needs to be checked every time, in case this has already been created
		}
		
		mElement.setName(node.getName());

		if (node.getExtends() != null && !node.getExtends().isEmpty()) {
			ClassVisitor classOrInterfaceVisitor = new ClassVisitor(mProject);
			for (ClassOrInterfaceType coi : node.getExtends()) {
				IJavaElement classOrInterface = mProject.find(coi.getName());
				if (classOrInterface == null) {
					classOrInterface = new JavaElement(coi.getName());
					classOrInterfaceVisitor.reset();
					classOrInterfaceVisitor.visit(coi, classOrInterface);
					mProject.addElement(classOrInterface);
				}
				mElement.addSuperClass(classOrInterface);
			}
		}
//		if (n.getImplements() != null && !n.getImplements().isEmpty())
//			for (ClassOrInterfaceType coi : n.getImplements())
//				classObject.addInterface(coi.getName());
//
//		for (BodyDeclaration member : n.getMembers()) {
//			if (member instanceof FieldDeclaration) {
//				variableVisitor.visit((FieldDeclaration) member, null);
//			}
//			if (member instanceof MethodDeclaration) {
//				methodVisitor.visit((MethodDeclaration) member, null);
//			}
//		}
	}
	
	@Override
	public void visit(ClassOrInterfaceType node, IJavaElement element) {
	}
	
	public IJavaElement getElement() {
		return mElement;
	}
	
	@Override
	public void reset() {
	}
	
}
