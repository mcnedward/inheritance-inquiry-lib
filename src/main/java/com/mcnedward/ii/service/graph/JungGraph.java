package com.mcnedward.ii.service.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.mcnedward.ii.exception.GraphBuildException;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;

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

	// Distance between graphs
	private static final int DEFAULT_X_DIST = 150;
	private static final int DEFAULT_Y_DIST = 300;
	private int mXDist;
	private int mYDist;

	public JungGraph() {
		this(DEFAULT_X_DIST, DEFAULT_Y_DIST);
	}

	public JungGraph(int xDist, int yDist) {
		mGraph = new DirectedSparseMultigraph<>();
		mNodeMap = new TreeMap<>();
		mEdgeMap = new TreeMap<>();
		mXDist = xDist;
		mYDist = yDist;
	}

	public void plotGraph(List<Node> nodes, List<Edge> edges) throws GraphBuildException {
		buildGraph(nodes, edges);
		initializeComponents();
		configure(mImageServer);
	}

	public void plotGraph(Stack<Node> nodes, Stack<Edge> edges) throws GraphBuildException {
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
		mLayout = new TreeLayout<>(mForest, mXDist, mYDist);
		mViewer = new VisualizationViewer<>(mLayout);
		mImageServer = new VisualizationImageServer<>(mViewer.getGraphLayout(), mViewer.getGraphLayout().getSize());
	}

	private void configure(BasicVisualizationServer<String, String> server) throws GraphBuildException {
		try {
			server.setBackground(Color.WHITE);

			RenderContext<String, String> context = server.getRenderContext();
			
			context.setVertexShapeTransformer(vertexShapeTransformer());
			context.setVertexFillPaintTransformer(vertexFillPaintTransformer());
			context.setVertexLabelTransformer(vertexLabelTransformer());
			context.setVertexLabelRenderer(vertexLabelRenderer());
			server.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

			context.setEdgeStrokeTransformer(edgeStrokeTransformer());
			context.setEdgeLabelTransformer(edgeLabelTransformer());
			context.setArrowDrawPaintTransformer(arrowFillPaintTransformer());
			context.setArrowFillPaintTransformer(arrowFillPaintTransformer());
			server.getRenderer().setEdgeRenderer(new ReverseEdgeRenderer<String, String>());
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

	private void buildGraph(Stack<Node> nodes, Stack<Edge> edges) {
		while (!nodes.isEmpty()) {
			Node node = nodes.pop();
			mGraph.addVertex(node.id());
			mNodeMap.put(node.id(), node);
		}
		while (!edges.isEmpty()) {
			Edge edge = edges.pop();
			mGraph.addEdge(edge.id(), edge.to().id(), edge.from().id());
			mEdgeMap.put(edge.id(), edge);
		}
	}

	private final Transformer<String, Paint> vertexFillPaintTransformer() {
		return new Transformer<String, Paint>() {
			@Override
			public Paint transform(String nodeName) {
				Node node = mNodeMap.get(nodeName);
				if (node.isInterface())
					return Color.LIGHT_GRAY;
				else
					return Color.WHITE;
			}
		};
	}

	private final Transformer<String, Shape> vertexShapeTransformer() {
		final FontMetrics fm = mViewer.getFontMetrics(mViewer.getFont());
		return new Transformer<String, Shape>() {
			@Override
			public Shape transform(String vertexName) {
				int width = fm.stringWidth(vertexName);
				int height = 30;
				int x = -(width / 2);
				int y = -15;
				Rectangle2D rect = new java.awt.geom.Rectangle2D.Double(x, y, width, height);
				rect.getBounds().grow(100, 250);
				return rect;
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

	private final DefaultVertexLabelRenderer vertexLabelRenderer() {
		return new DefaultVertexLabelRenderer(Color.BLACK) {
			private static final long serialVersionUID = 1909972527171078432L;

			public <V> Component getVertexLabelRendererComponent(JComponent vv, Object nodeName, Font font, boolean isSelected, V vertex) {
				super.setForeground(Color.BLACK);

				Node node = null;
				for (Map.Entry<String, Node> entry : mNodeMap.entrySet()) {
					Node n = entry.getValue();
					if (n.name().equals(nodeName))
						node = n;
				}
				int fontStyle;
				if (node != null && node.isInterface()) {
					fontStyle = Font.ITALIC;
				} else {
					fontStyle = Font.PLAIN;
				}
				
				Font currentFont;
				if (font != null) {
					currentFont = font;
				} else {
					currentFont = vv.getFont();
				}
				Font theFont = new Font(currentFont.getName(), fontStyle, 18);
				setFont(theFont);
				setIcon(null);
				setBorder(noFocusBorder);
				setValue(nodeName);
				return this;
			}
		};
	}

	private final Transformer<String, Stroke> edgeStrokeTransformer() {
		return new Transformer<String, Stroke>() {
			private final Stroke NORMAL = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
			private final Stroke DASHED = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
			@Override
			public Stroke transform(String edgeName) {
				Edge edge = mEdgeMap.get(edgeName);
				if (edge.isImplements())
					return DASHED;
				return NORMAL; 
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
	
	private final Transformer<String, Paint> arrowFillPaintTransformer() {
		return new Transformer<String, Paint>() {
			@Override
			public Paint transform(String edgeName) {
				Edge edge = mEdgeMap.get(edgeName);
				if (edge.isImplements())
					return Color.WHITE;
				else
					return Color.BLACK;
			}
		};
	}
}
