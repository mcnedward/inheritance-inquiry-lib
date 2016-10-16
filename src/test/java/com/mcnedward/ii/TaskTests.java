package com.mcnedward.ii;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.mcnedward.ii.element.JavaElement;

/**
 * @author Edward - Jul 24, 2016
 *
 */
public class TaskTests {

	@Test
	public void testRef() {
		JavaElement element1 = new JavaElement("1");
		String name = element1.getName();
		JavaElement element2 = new JavaElement(name);
		
		assertThat(element1.getName() == element2.getName(), is(true));
	}
	
	
}
