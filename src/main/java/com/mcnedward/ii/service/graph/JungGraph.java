package com.mcnedward.ii.service.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.mcnedward.ii.exception.GraphBuildException;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

/**
 * @author Edward - Jul 18, 2016
 *
 */
public class JungGraph extends JFrame {
	private static final long serialVersionUID = 1L;

	private DirectedGraph<String, String> mGraph;
	private DelegateForest<String, String> mForest;
	private Layout<String, String> mLayout;
	private VisualizationImageServer<String, String> mImageServer;
	private VisualizationViewer<String, String> mViewer;
	private Map<String, Node> mNodeMap;
	private Map<String, Edge> mEdgeMap;

	public JungGraph() {
		mGraph = new DirectedSparseMultigraph<>();
		mNodeMap = new TreeMap<>();
		mEdgeMap = new TreeMap<>();
	}

	public void plotGraph(List<Node> nodes, List<Edge> edges) throws GraphBuildException {
		buildGraph(nodes, edges);
		initializeComponents();
		configure(mImageServer);
	}

	public BufferedImage createImage() {
		BufferedImage image = (BufferedImage) mImageServer.getImage(
				new Point2D.Double(mViewer.getGraphLayout().getSize().getWidth() / 2, mViewer.getGraphLayout().getSize().getHeight() / 2),
				new Dimension(mViewer.getGraphLayout().getSize()));
		return image;
	}
	
	private void initializeComponents() {
		mForest = new DelegateForest<>(mGraph);
		mLayout = new TreeLayout<>(mForest, 200, 200);
		mViewer = new VisualizationViewer<>(mLayout);
		mImageServer = new VisualizationImageServer<>(mViewer.getGraphLayout(), mViewer.getGraphLayout().getSize());
	}

	private void configure(BasicVisualizationServer<String, String> server) throws GraphBuildException {
		try {
			server.setBackground(Color.WHITE);

			server.getRenderContext().setVertexShapeTransformer(vertexShapeTransformer());
			server.getRenderContext().setVertexFillPaintTransformer(vertexFillPaintTransformer());
			server.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer());
			server.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.E);

			server.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer());
			server.getRenderContext().setEdgeLabelTransformer(edgeLabelTransformer());
		} catch (Exception e) {
			throw new GraphBuildException("There was a problem with configuring the graph...", e);
		}
	}

	private void buildGraph(List<Node> nodes, List<Edge> edges) {
		for (Node node : nodes) {
			mGraph.addVertex(node.id());
			mNodeMap.put(node.id(), node);
		}
		for (Edge edge : edges) {
			mGraph.addEdge(edge.id(), edge.to().id(), edge.from().id());
			mEdgeMap.put(edge.id(), edge);
		}
	}

	private final Transformer<String, Paint> vertexFillPaintTransformer() {
		return new Transformer<String, Paint>() {
			@Override
			public Paint transform(String input) {
				return Color.GREEN;
			}
		};
	}

	private final Transformer<String, Shape> vertexShapeTransformer() {
		return new Transformer<String, Shape>() {
			@Override
			public Shape transform(String vertexName) {
				return new VertexShapeFactory<String>().getEllipse(vertexName);
			}
		};
	}

	private final Transformer<String, String> vertexLabelTransformer() {
		return new Transformer<String, String>() {
			@Override
			public String transform(String nodeName) {
				Node node = mNodeMap.get(nodeName);
				return node.name();
			}
		};
	}

	private final Transformer<String, Stroke> edgeStrokeTransformer() {
		return new Transformer<String, Stroke>() {
			@Override
			public Stroke transform(String s) {
				return new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
			}
		};
	}

	private final Transformer<String, String> edgeLabelTransformer() {
		return new Transformer<String, String>() {
			@Override
			public String transform(String edgeName) {
				Edge edge = mEdgeMap.get(edgeName);
				return edge.name();
			}
		};
	}
}
