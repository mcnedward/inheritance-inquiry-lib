package com.mcnedward.ii.jdt.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaProject;

/**
 * This is a visitor for a {@link JavaElement} that is either a superclass or interface for another JavaElement.
 * 
 * @author Edward - Jun 16, 2016
 * 
 */
public class ClassOrInterfaceVisitor extends JavaElementVisitor {
	protected static final Logger logger = Logger.getLogger(ClassOrInterfaceVisitor.class);

	private boolean mIsInterface;

	public ClassOrInterfaceVisitor(JavaProject project, JavaElement parentElement) {
		super(project, parentElement);
	}

	@Override
	public boolean visit(ParameterizedType node) {
		String name = node.getType().toString();
		ITypeBinding binding = node.resolveBinding();

		JavaElement element = findOrCreateElement(name, binding, mIsInterface);
		
		ClassOrInterfaceElement coi = new ClassOrInterfaceElement(element);
		parentElement().addClassOrInterface(coi);

		// Add any generic type arguments
		CoiTypeArgVisitor coiTypeVisitor = new CoiTypeArgVisitor(project(), coi);
		coiTypeVisitor.setGenericArgs(parentElement().getGenericTypeArgs());

		@SuppressWarnings("unchecked")
		List<Type> typeArguments = node.typeArguments();

		if (element != null) {
			for (Type t : typeArguments) {
				t.accept(coiTypeVisitor);
			}
		}
		// False to prevent visiting SimpleTypes using the visit() in this class.
		// Use the SimpleTypeVisitor for that by accepting the typeArguments
		return false;
	}

	@Override
	public boolean visit(SimpleType node) {
		// Add any generic type arguments
		SimpleTypeVisitor simpleTypeVisitor = new SimpleTypeVisitor(project(), parentElement());
		simpleTypeVisitor.setIsInterface(mIsInterface);
		node.accept(simpleTypeVisitor);
		return super.visit(node);
	}

	public void setIsInterface(boolean isInterface) {
		mIsInterface = isInterface;
	}

}
