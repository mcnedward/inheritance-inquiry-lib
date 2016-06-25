package com.mcnedward.ii.visitor;

import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaMethod;
import com.mcnedward.ii.element.JavaProject;

/**
 * @author Edward - Jun 18, 2016
 *
 */
public class ClassVisitor extends BaseVisitor<JavaElement> {
	protected static final Logger logger = Logger.getLogger(ClassVisitor.class);

	private ClassOrInterfaceTypeVisitor mClassOrInterfaceTypeVisitor;
	private MethodVisitor mMethodVisitor;

	public ClassVisitor(JavaProject project) {
		super(project);
		mClassOrInterfaceTypeVisitor = new ClassOrInterfaceTypeVisitor(project);
		mMethodVisitor = new MethodVisitor(project);
	}

	@Override
	public void visit(PackageDeclaration node, JavaElement element) {
		element.setPackageName(node.getPackageName());
	}

	@Override
	public void visit(ImportDeclaration node, JavaElement element) {
		element.addImport(node.getName().toString());
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration node, JavaElement element) {
		String elementName = node.getName();
		boolean isInterface = node.isInterface();
		// First try and find this class or interface in the JavaProject
		element = project().findOrCreateElement(element.getPackageName(), elementName);

		element.setIsInterface(isInterface);
		element.setNeedsInterfaceStatusChecked(false); // Doesn't need to be checked after build anymore
		element.setName(node.getName());
		
		List<ClassOrInterfaceType> classExtends = node.getExtends();
		for (ClassOrInterfaceType type : classExtends) {
			mClassOrInterfaceTypeVisitor.setIsInterface(element.isInterface());
			mClassOrInterfaceTypeVisitor.visit(type, element);
		}
		
		List<ClassOrInterfaceType> classInterfaces = node.getImplements();
		for (ClassOrInterfaceType type : classInterfaces) {
			mClassOrInterfaceTypeVisitor.setIsInterface(true);
			mClassOrInterfaceTypeVisitor.visit(type, element);
		}
		
		for (BodyDeclaration member : node.getMembers()) {
			if (member instanceof MethodDeclaration) {
				JavaMethod method = new JavaMethod();
				mMethodVisitor.visit((MethodDeclaration) member, method);
				element.addMethod(method);
			}
		}
	}

}
