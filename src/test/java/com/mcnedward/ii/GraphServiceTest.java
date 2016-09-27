package com.mcnedward.ii;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.mcnedward.ii.listener.GraphLoadListener;
import com.mcnedward.ii.service.graph.IGraphService;
import com.mcnedward.ii.service.graph.element.GraphOptions;
import com.mcnedward.ii.service.graph.jung.JungGraph;
import com.mcnedward.ii.utils.ServiceFactory;
import org.junit.Test;

import com.mcnedward.ii.element.ClassOrInterfaceElement;
import com.mcnedward.ii.element.JavaElement;
import com.mcnedward.ii.element.JavaSolution;
import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.DitHierarchy;

import java.util.List;

/**
 * @author Edward - Jul 28, 2016
 *
 */
public class GraphServiceTest extends ProjectTest {

	@Test
	public void service_buildsCorrectGraphForInheritance() throws GraphBuildException {
		// Arrange
		JavaElement base = new JavaElement("base");
		ClassOrInterfaceElement baseCoi = new ClassOrInterfaceElement(base);
		JavaElement parent1 = new JavaElement("parent1");
		ClassOrInterfaceElement parent1Coi = new ClassOrInterfaceElement(parent1);
		JavaElement parent2 = new JavaElement("parent2");
		ClassOrInterfaceElement parent2Coi = new ClassOrInterfaceElement(parent2);
		JavaElement element = new JavaElement("class");

		element.addClassOrInterface(parent2Coi);
		parent2.addClassOrInterface(parent1Coi);
		parent1.addClassOrInterface(baseCoi);

		DitHierarchy hierarchy = new DitHierarchy(element);
		JavaSolution solution = new JavaSolution("Test Inheritance", "Test System", "1");
		solution.addDitHierarchy(hierarchy);

		IGraphService service = ServiceFactory.ditGraphService();
        GraphOptions options = new GraphOptions();
        options.setSolution(solution);

		// Act
		service.buildHierarchyGraphs(options, new GraphLoadListener() {
            @Override
            public void onGraphsLoaded(List<JungGraph> graphs) {
                // Assert
                assertThat(graphs.isEmpty(), is(false));
            }

            @Override
            public void onProgressChange(String message, int progress) {

            }

            @Override
            public void onBuildError(String message, Exception exception) {

            }
        });
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
		JavaElement element = new JavaElement("interface");

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
		
		IGraphService service = ServiceFactory.ditGraphService();
        GraphOptions options = new GraphOptions();
        options.setSolution(solution);

		// Act
        service.buildHierarchyGraphs(options, new GraphLoadListener() {
            @Override
            public void onGraphsLoaded(List<JungGraph> graphs) {
                // Assert
                assertThat(graphs.isEmpty(), is(false));
            }

            @Override
            public void onProgressChange(String message, int progress) {

            }

            @Override
            public void onBuildError(String message, Exception exception) {

            }
        });
	}

}
