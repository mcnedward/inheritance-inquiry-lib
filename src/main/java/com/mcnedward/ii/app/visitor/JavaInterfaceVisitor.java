package com.mcnedward.ii.app.visitor;

import com.mcnedward.ii.app.element.IJavaElement;
import com.mcnedward.ii.app.element.JavaElement;
import com.mcnedward.ii.app.element.JavaProject;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class JavaInterfaceVisitor extends ProjectVisitor {

	private IJavaElement mInterface;
	private IJavaElement mClass;
	
	public JavaInterfaceVisitor(JavaProject project, IJavaElement javaClass, String interfaceName) {
		super(project);
		mInterface = project().findInterface(interfaceName);
		if (mInterface == null) {
			mInterface = new JavaElement(interfaceName);
			project().addElement(mInterface);
		}		
		mInterface.setIsInterface(true);
		mClass = javaClass;
		mClass.addInterface(mInterface);
	}
	
	@Override
	protected IJavaElement getJavaElement() {
		return mInterface;
	}

	
}
