package com.mcnedward.ii.app.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.mcnedward.ii.app.element.IJavaElement;
import com.mcnedward.ii.app.element.JavaElement;
import com.mcnedward.ii.app.element.JavaProject;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaElementVisitor extends ProjectVisitor {
	private static final Logger logger = Logger.getLogger(JavaElementVisitor.class);
	
	private IJavaElement mJavaElement;

	public JavaElementVisitor(JavaProject project, String elementName) {
		super(project);
		mJavaElement = project().find(elementName);
		if (mJavaElement == null) {
			// Element is not in project yet, so add it
			mJavaElement = new JavaElement(elementName);
			project().addElement(mJavaElement);
		}
	}

	@Override
	protected IJavaElement getJavaElement() {
		return mJavaElement;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(TypeDeclaration node) {
		try {
			mJavaElement.setIsInterface(node.isInterface());
			
			// Setup all the interfaces
			List<ASTNode> interfaces = node.superInterfaceTypes();
			for (ASTNode inter : interfaces) {
				inter.accept(new JavaInterfaceVisitor(project(), mJavaElement, inter.toString()));
			}

			// Set the super class
			Type superClassType = node.getSuperclassType();
			if (superClassType != null) {
				superClassType.accept(new JavaElementVisitor(project(), "super"));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
