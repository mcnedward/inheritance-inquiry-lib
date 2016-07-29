package com.mcnedward.ii;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.GraphService;
import com.mcnedward.ii.service.graph.element.DitHierarchy;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public class GraphServiceTest extends ProjectTest {

	@Test
	@Ignore
	public void service_buildsCorrectGraphForInheritance() throws GraphBuildException {
		// Arrange
		JavaElement base = new JavaElement("base");
		ClassOrInterfaceElement baseCoi = new ClassOrInterfaceElement(base);
		JavaElement parent1 = new JavaElement("parent1");
		ClassOrInterfaceElement parent1Coi = new ClassOrInterfaceElement(parent1);
		JavaElement parent2 = new JavaElement("parent2");
		ClassOrInterfaceElement parent2Coi = new ClassOrInterfaceElement(parent2);
		JavaElement element = new JavaElement("element");

		element.addClassOrInterface(parent2Coi);
		parent2.addClassOrInterface(parent1Coi);
		parent1.addClassOrInterface(baseCoi);

		DitHierarchy hierarchy = new DitHierarchy(element);
		JavaSolution solution = new JavaSolution("Test Inheritance", "Test System", "1");
		solution.addDitHierarchy(hierarchy);

		GraphService service = new GraphService();

		// Act
		boolean built = service.buildDitHierarchyTreeGraph(solution);

		// Assert
		assertThat(built, is(true));
	}
	
	@Test
	public void service_buildsCorrectGraphForInterface() throws GraphBuildException {
		// Arrange
		JavaElement base = new JavaElement("base");
		ClassOrInterfaceElement baseCoi = new ClassOrInterfaceElement(base);
		JavaElement parent1 = new JavaElement("parent1");
		ClassOrInterfaceElement parent1Coi = new ClassOrInterfaceElement(parent1);
		JavaElement parent2 = new JavaElement("parent2");
		ClassOrInterfaceElement parent2Coi = new ClassOrInterfaceElement(parent2);
		JavaElement element = new JavaElement("element");

		base.setIsInterface(true);
		parent1.setIsInterface(true);
		parent2.setIsInterface(true);
		element.setIsInterface(true);

		element.addClassOrInterface(parent2Coi);
		element.addClassOrInterface(parent1Coi);
		parent1.addClassOrInterface(baseCoi);
		
		DitHierarchy hierarchy = new DitHierarchy(element);
		JavaSolution solution = new JavaSolution("Test Interface", "Test System", "1");
		solution.addDitHierarchy(hierarchy);
		
		GraphService service = new GraphService();

		// Act
		boolean built = service.buildDitHierarchyTreeGraph(solution);

		// Assert
		assertThat(built, is(true));
	}

}
