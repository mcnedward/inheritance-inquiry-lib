package com.mcnedward.ii;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Stack;

import org.junit.Test;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.service.graph.element.DitHierarchy;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class DitHeirarchyTreeTests {

	@Test
	public void HeirarchyTree_buildsCorrectTreeForInheritance() {
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

		// Act
		DitHierarchy hierarchy = new DitHierarchy(element);
		Stack<List<DitHierarchy>> tree = hierarchy.tree;

		String expectedTree = "base parent1 parent2 element";
		String actualTree = "";
		for (List<DitHierarchy> h : tree) {
			actualTree += h.get(0).element + " ";
		}
		actualTree += hierarchy.element;

		// Assert
		assertThat(actualTree, is(expectedTree));
	}

	@Test
	public void HeirarchyTree_buildsCorrectTreeForInterface() {
		// Arrange
		JavaElement base = new JavaElement("base");
		base.setIsInterface(true);
		ClassOrInterfaceElement baseCoi = new ClassOrInterfaceElement(base);
		JavaElement parent1 = new JavaElement("parent1");
		parent1.setIsInterface(true);
		ClassOrInterfaceElement parent1Coi = new ClassOrInterfaceElement(parent1);
		JavaElement parent2 = new JavaElement("parent2");
		parent2.setIsInterface(true);
		ClassOrInterfaceElement parent2Coi = new ClassOrInterfaceElement(parent2);
		JavaElement element = new JavaElement("element");
		element.setIsInterface(true);

		element.addClassOrInterface(parent2Coi);
		element.addClassOrInterface(parent1Coi);
		parent1.addClassOrInterface(baseCoi);

		// Act
		DitHierarchy hierarchy = new DitHierarchy(element);
		Stack<List<DitHierarchy>> tree = hierarchy.tree;

		String expectedTree = "0 base 1 parent2 1 parent1 2 element";
		String actualTree = "";
		for (int i = 0; i < tree.size(); i++) {
			List<DitHierarchy> list = tree.get(i);
			for (int j = 0; j < list.size(); j++) {
				DitHierarchy d = list.get(j);
				actualTree += i + " " + d.element + " ";
			}
		}
		actualTree += "2 element";

		// Assert
		assertThat(actualTree, is(expectedTree));
	}

	@Test
	public void HierarchyTree_findsCorrectDIT() {
		// Arrange
		JavaElement base = new JavaElement("base");
		base.setIsInterface(true);
		ClassOrInterfaceElement baseCoi = new ClassOrInterfaceElement(base);
		JavaElement parent1 = new JavaElement("parent1");
		parent1.setIsInterface(true);
		ClassOrInterfaceElement parent1Coi = new ClassOrInterfaceElement(parent1);
		JavaElement parent2 = new JavaElement("parent2");
		parent2.setIsInterface(true);
		ClassOrInterfaceElement parent2Coi = new ClassOrInterfaceElement(parent2);
		JavaElement element = new JavaElement("element");
		element.setIsInterface(true);

		element.addClassOrInterface(parent2Coi);
		element.addClassOrInterface(parent1Coi);
		parent1.addClassOrInterface(baseCoi);

		// Act
		DitHierarchy tree = new DitHierarchy(element);
		int dit = tree.dit;

		// Assert
		assertThat(dit, is(2));
	}

}
