package com.mcnedward.ii;

import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.exception.TaskBuildException;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
