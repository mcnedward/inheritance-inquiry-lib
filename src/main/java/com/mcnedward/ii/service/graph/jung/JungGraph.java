package com.mcnedward.ii.service.graph.jung;

import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.GType;
import com.mcnedward.ii.service.graph.element.Node;
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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * @author Edward - Jul 18, 2016
 */
public class JungGraph {

    private DirectedGraph<String, String> mGraph;
    private DelegateForest<String, String> mForest;
    private TreeLayout mLayout;
    private VisualizationImageServer<String, String> mImageServer;
    private VisualizationViewer<String, String> mViewer;
    private GraphZoomScrollPane mGraphPane;
    private Map<String, Node> mNodeMap;
    private Map<String, Edge> mEdgeMap;

    private String mFullyQualifiedName;
    private GType mType;
    // Distance between graphs
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 400;
    private static final int DEFAULT_X_DIST = 200;
    private static final int DEFAULT_Y_DIST = 100;
    private int mWidth, mHeight, mXDist, mYDist;

    public JungGraph(String fullyQualifiedName) {
        this(fullyQualifiedName, null, null, DEFAULT_X_DIST, DEFAULT_Y_DIST);
    }

    public JungGraph(String fullyQualifiedName, GType type) {
        this(fullyQualifiedName, null, null, DEFAULT_X_DIST, DEFAULT_Y_DIST);
        mType = type;
    }

    public JungGraph(String fullyQualifiedName, Integer width, Integer height) {
        this(fullyQualifiedName, width, height, DEFAULT_X_DIST, DEFAULT_Y_DIST);
    }

    public JungGraph(String fullyQualifiedName, Integer width, Integer height, Integer xDist, Integer yDist) {
        mFullyQualifiedName = fullyQualifiedName;
        mWidth = width == null ? DEFAULT_WIDTH : width;
        mHeight = height == null ? DEFAULT_HEIGHT : height;
        mXDist = xDist == null ? DEFAULT_X_DIST : xDist;
        mYDist = yDist == null ? DEFAULT_Y_DIST : yDist;
        mType = GType.H_TREE;
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
        initializeLayout(mLayout);
        mViewer = new VisualizationViewer<>(mLayout);
        mViewer.setAlignmentX(Component.CENTER_ALIGNMENT);
        mViewer.setAlignmentY(Component.CENTER_ALIGNMENT);
        mImageServer = new VisualizationImageServer<>(mViewer.getGraphLayout(), mViewer.getGraphLayout().getSize());
        mGraphPane = new GraphZoomScrollPane(mViewer);
    }

    protected void initializeLayout(TreeLayout layout) {
        // Override this in subclasses, if needed
    }

    private void configure(BasicVisualizationServer<String, String> server) throws GraphBuildException {
        try {
            server.setBackground(Color.WHITE);
            RenderContext<String, String> context = server.getRenderContext();

            context.setVertexLabelTransformer(vertexLabelTransformer(mNodeMap));
            context.setVertexLabelRenderer(vertexLabelRenderer(mNodeMap));
            context.setVertexShapeTransformer(vertexShapeTransformer(mViewer, mNodeMap));
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

    protected Transformer<String, Paint> vertexFillPaintTransformer() {
        return nodeName -> {
            Node node = mNodeMap.get(nodeName);
            if (node.isInterface())
                return Color.LIGHT_GRAY;
            else
                return new Color(80, 95, 110);
        };
    }

    protected Transformer<String, Shape> vertexShapeTransformer(VisualizationViewer<String, String> vv, Map<String, Node> nodeMap) {
        final FontMetrics fm = vv.getFontMetrics(vv.getFont());
        return vertexName -> {
            Node node = nodeMap.get(vertexName);
            float width = fm.stringWidth(node.name()) * 1.25f;
            float height = fm.getHeight() * 1.25f;
            float x = -(width / 2.0f);
            float y = -(height / 2.0f);
            new VertexShapeFactory<String>().getRectangle(vertexName);
            Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
            return rect;
//                 Rectangle frame = new VertexShapeFactory<String>().getRectangle(vertexName).getBounds();
//                 frame.grow(20, 20);
//                 Rectangle rect = new Rectangle(frame);
//                 return rect;
        };
    }

    protected Transformer<String, String> vertexLabelTransformer(Map<String, Node> nodeMap) {
        return nodeName -> {
            Node node = nodeMap.get(nodeName);
            return node.name();
        };
    }

    protected DefaultVertexLabelRenderer vertexLabelRenderer(Map<String, Node> nodeMap) {
        return new DefaultVertexLabelRenderer(Color.BLACK) {
            private static final long serialVersionUID = 1909972527171078432L;

            public <V> Component getVertexLabelRendererComponent(JComponent vv, Object nodeName, Font font, boolean isSelected, V vertex) {
                super.setForeground(Color.BLACK);

                Node node = null;
                for (Map.Entry<String, Node> entry : nodeMap.entrySet()) {
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
                Font theFont = new Font(currentFont.getName(), fontStyle, 10);
                setFont(theFont);
                setIcon(null);
                setBorder(noFocusBorder);
                setValue(nodeName);
                return this;
            }
        };
    }

    protected final Transformer<String, Stroke> edgeStrokeTransformer() {
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

    protected final Transformer<String, String> edgeLabelTransformer() {
        return edgeName -> {
            Edge edge = mEdgeMap.get(edgeName);
            return edge.name();
        };
    }

    protected final Transformer<String, Paint> arrowFillPaintTransformer() {
        return edgeName -> {
            Edge edge = mEdgeMap.get(edgeName);
            if (edge.isImplements())
                return Color.WHITE;
            else
                return Color.BLACK;
        };
    }

    public String getElementName() {
        return mFullyQualifiedName;
    }

    public GType getType() {
        return mType;
    }

    public GraphZoomScrollPane getGraphPane() {
        return mGraphPane;
    }
}
