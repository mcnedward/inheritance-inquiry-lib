package com.mcnedward.ii.service.graph;

import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.GType;
import com.mcnedward.ii.service.graph.element.Node;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * @author Edward - Jul 18, 2016
 */
public class JungGraph {
    private static final long serialVersionUID = 1L;

    private DirectedGraph<String, String> mGraph;
    private DelegateForest<String, String> mForest;
    private Layout mLayout;
    private VisualizationImageServer<String, String> mImageServer;
    private VisualizationViewer<String, String> mViewer;
    private GraphZoomScrollPane mGraphPane;
    private Map<String, Node> mNodeMap;
    private Map<String, Edge> mEdgeMap;

    private String mElementName;
    private GType mType;
    // Distance between graphs
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 400;
    private static final int DEFAULT_X_DIST = 50;
    private static final int DEFAULT_Y_DIST = 50;
    private int mWidth, mHeight, mXDist, mYDist;

    public JungGraph(String element) {
        this(element, GType.H_TREE, null, null, DEFAULT_X_DIST, DEFAULT_Y_DIST);
    }

    public JungGraph(String element, GType type) {
        this(element, type, null, null, DEFAULT_X_DIST, DEFAULT_Y_DIST);
    }

    public JungGraph(String element, Integer width, Integer height) {
        this(element, GType.H_TREE, width, height, DEFAULT_X_DIST, DEFAULT_Y_DIST);
    }

    public JungGraph(String element, GType type, Integer width, Integer height, Integer xDist, Integer yDist) {
        mElementName = element;
        mType = type;
        mWidth = width == null ? DEFAULT_WIDTH : width;
        mHeight = height == null ? DEFAULT_HEIGHT : height;
        mXDist = xDist == null ? DEFAULT_X_DIST : xDist;
        mYDist = yDist == null ? DEFAULT_Y_DIST : yDist;
        mGraph = new DirectedSparseMultigraph<>();
        mNodeMap = new TreeMap<>();
        mEdgeMap = new TreeMap<>();

    }

    public void plotGraph(List<Node> nodes, List<Edge> edges) throws GraphBuildException {
        buildGraph(nodes, edges);
        initializeComponents();
        configure(mViewer);
        configure(mImageServer);
    }

    public void plotGraph(Stack<Node> nodes, Stack<Edge> edges) throws GraphBuildException {
        buildGraph(nodes, edges);
        initializeComponents();
        configure(mViewer);
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
        mGraphPane = new GraphZoomScrollPane(mViewer);
    }

    private void configure(BasicVisualizationServer<String, String> server) throws GraphBuildException {
        try {
            server.setBackground(Color.WHITE);

            RenderContext<String, String> context = server.getRenderContext();

            context.setVertexLabelTransformer(vertexLabelTransformer());
            context.setVertexLabelRenderer(vertexLabelRenderer());
            context.setVertexShapeTransformer(vertexShapeTransformer());
            context.setVertexFillPaintTransformer(vertexFillPaintTransformer());
            server.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

            context.setEdgeStrokeTransformer(edgeStrokeTransformer());
            context.setEdgeLabelTransformer(edgeLabelTransformer());
            context.setArrowDrawPaintTransformer(arrowFillPaintTransformer());
            context.setArrowFillPaintTransformer(arrowFillPaintTransformer());
            server.getRenderer().setEdgeRenderer(new ReverseEdgeRenderer<>());
        } catch (Exception e) {
            throw new GraphBuildException("There was a problem configuring the graph...", e);
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
                    return new Color(50, 70, 160);
            }
        };
    }

    private final Transformer<String, Shape> vertexShapeTransformer() {
        final FontMetrics fm = mViewer.getFontMetrics(mViewer.getFont());
        return new Transformer<String, Shape>() {
            @Override
            public Shape transform(String vertexName) {
//                int width = (int) (fm.stringWidth(vertexName) * 1.25);
//                int height = 50;
//                int x = (int) -(width / 1.25);
//                int y = -15;
//                Rectangle2D rect = new java.awt.geom.Rectangle2D.Double(x, y, width, height);
//                return rect;

                 Rectangle frame = new VertexShapeFactory<String>().getRectangle(vertexName).getBounds();
                 frame.grow(20, 20);
                 Rectangle rect = new Rectangle(frame);
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
                Font theFont = new Font(currentFont.getName(), fontStyle, 14);
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

    public String getElementName() {
        return mElementName;
    }

    public GType getType() {
        return mType;
    }

    public GraphZoomScrollPane getGraphPane() {
        return mGraphPane;
    }
}
