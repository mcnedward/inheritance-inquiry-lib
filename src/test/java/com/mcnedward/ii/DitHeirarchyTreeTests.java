package com.mcnedward.ii;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Stack;

import org.junit.Test;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.service.graph.element.InheritanceTree;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class HeirarchyTreeTests {

	@Test
	public void HeirarchyTree_buildsCorrectTree() {
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
		InheritanceTree hTree = new InheritanceTree(element);
		Stack<String> tree = hTree.inheritanceTree;

		String expectedTree = "base parent1 parent2 element ";
		String actualTree = "";
		while (!tree.isEmpty()) {
			String t = tree.pop();
			actualTree += t + " ";
		}

		// Assert
		assertThat(expectedTree, is(actualTree));
	}

}
