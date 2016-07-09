package com.mcnedward.ii.element.generic;

import com.mcnedward.ii.element.JavaElement;

/**
 * Represents a the {@link JavaElement} that is used in the place of a {@link GenericParameter}.
 * <p>For example, if the superclass is:</p>
 * <li>public class BaseRepository&lt;T&gt;</li>
 * <p>And the child class is:</p>
 * <li>public class AccountRepository extends BaseRepository&lt;Account&gt;</li>
 * <p>Then the GenericParameter for this ResolvedGeneric would be the T, and the JavaElement would be Account.</p>
 * 
 * @author Edward - Jul 9, 2016
 *
 */
public class ResolvedGeneric {

	private GenericParameter mGeneric;
	private JavaElement mElement;
	
	public ResolvedGeneric(GenericParameter generic, JavaElement element) {
		mGeneric = generic;
		mElement = element;
	}
	
	public GenericParameter getGeneric() {
		return mGeneric;
	}
	
	public JavaElement getElement() {
		return mElement;
	}
	
}
