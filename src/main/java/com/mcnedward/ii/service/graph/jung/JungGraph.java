package com.mcnedward.ii.service.graph.jung;

import com.mcnedward.ii.exception.GraphBuildException;
import com.mcnedward.ii.service.graph.element.Edge;
import com.mcnedward.ii.service.graph.element.GraphOptions;
import com.mcnedward.ii.service.graph.element.Node;
import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.IIUtils;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
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
    private ScalingControl mScaler;
    private GraphZoomScrollPane mGraphPane;
    private Map<String, Node> mNodeMap;
    private Map<String, Edge> mEdgeMap;

    private String mFullyQualifiedName;
    private String mElementName;
    private int mXDist, mYDist;

    public JungGraph(String fullyQualifiedName) {
        this(fullyQualifiedName, new GraphOptions());
    }

    public JungGraph(String fullyQualifiedName, GraphOptions options) {
        mFullyQualifiedName = fullyQualifiedName;
        mElementName = IIUtils.getElementNameFromPackage(fullyQualifiedName);
        mXDist = options.getXDist();
        mYDist = options.getYDist();
        mGraph = new DirectedSparseMultigraph<>();
        mNodeMap = new TreeMap<>();
        mEdgeMap = new TreeMap<>();
    }

    public void update(GraphOptions options) throws GraphBuildException {
        initializeComponents(options);
        configure(mViewer, options);
        configure(mImageServer, options);
    }

    public void plotGraph(List<Node> nodes, List<Edge> edges) throws GraphBuildException {
        buildGraph(nodes, edges);
        update(new GraphOptions());
    }

    public void plotGraph(Stack<Node> nodes, Stack<Edge> edges) throws GraphBuildException {
        buildGraph(nodes, edges);
        update(new GraphOptions());
    }

    public BufferedImage createImage() {
        BufferedImage image = (BufferedImage) mImageServer.getImage(
                new Point2D.Double(mViewer.getGraphLayout().getSize().getWidth() / 2, mViewer.getGraphLayout().getSize().getHeight() / 2),
                new Dimension(mViewer.getGraphLayout().getSize()));
        return image;
    }

    public void setZoom(int zoom) {
        mScaler.scale(mViewer, zoom > 0 ? 1.1f : 1 / 1.1f, mViewer.getCenter());
    }

    private void initializeComponents(GraphOptions options) {
        mForest = new DelegateForest<>(mGraph);
        mLayout = new TreeLayout<>(mForest, options.getXDist(), options.getYDist());
        initializeLayout(mLayout);

        mViewer = new VisualizationViewer<>(mLayout);
        mViewer.setAlignmentX(Component.CENTER_ALIGNMENT);
        mViewer.setAlignmentY(Component.CENTER_ALIGNMENT);
        mViewer.setVertexToolTipTransformer(vertexToolTipTransformer(mNodeMap));
        mViewer.setEdgeToolTipTransformer(edgeToolTipTransformer(mEdgeMap));

        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        mViewer.setGraphMouse(gm);

        mScaler = new CrossoverScalingControl();
        mViewer.scaleToLayout(mScaler);

        mImageServer = new VisualizationImageServer<>(mViewer.getGraphLayout(), mViewer.getGraphLayout().getSize());
        mGraphPane = new GraphZoomScrollPane(mViewer);
    }

    protected void initializeLayout(TreeLayout layout) {
        // Override this in subclasses, if needed
    }

    private void configure(BasicVisualizationServer<String, String> server, GraphOptions options) throws GraphBuildException {
        try {
            server.setBackground(Color.WHITE);
            RenderContext<String, String> context = server.getRenderContext();

            context.setVertexFontTransformer(vertexFontTransformer(mViewer, options));
            context.setVertexLabelTransformer(vertexLabelTransformer(mNodeMap));
            context.setVertexLabelRenderer(vertexLabelRenderer(mNodeMap, options));
            context.setVertexShapeTransformer(vertexShapeTransformer(mViewer, mNodeMap, options));
            context.setVertexFillPaintTransformer(vertexFillPaintTransformer(options));
            server.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

            context.setEdgeStrokeTransformer(edgeStrokeTransformer());
            context.setEdgeLabelTransformer(edgeLabelTransformer());
            context.setEdgeLabelRenderer(edgeLabelRenderer(options));

            context.setArrowDrawPaintTransformer(arrowFillPaintTransformer(options));
            context.setArrowFillPaintTransformer(arrowFillPaintTransformer(options));
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

    protected Transformer<String,Font> vertexFontTransformer(VisualizationViewer<String, String> vv, GraphOptions options) {
        return s -> vv.getFont().deriveFont(options.getFont().getSize());
    }

    protected Transformer<String, Paint> vertexFillPaintTransformer(GraphOptions options) {
        return nodeName -> {
            Node node = mNodeMap.get(nodeName);
            if (node.isInterface())
                return options.getInterfaceLabelColor();
            else {
                return options.getLabelColor();
            }
        };
    }

    protected Transformer<String, Shape> vertexShapeTransformer(VisualizationViewer<String, String> vv, Map<String, Node> nodeMap, GraphOptions options) {
        final FontMetrics fm = vv.getFontMetrics(options.getFont());
        return vertexName -> {
            Node node = nodeMap.get(vertexName);
            float width = fm.stringWidth(node.name()) * 1.25f;
            float height = fm.getHeight() * 1.25f;
            float x = -(width / 2.0f);
            float y = -(height / 2.0f);
            Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
            IILogger.info("Font size %s width %s", fm.getFont().getSize(), width);
            return rect;
        };
    }

    protected Transformer<String, String> vertexLabelTransformer(Map<String, Node> nodeMap) {
        return nodeName -> {
            Node node = nodeMap.get(nodeName);
            return node.name();
        };
    }

    protected DefaultVertexLabelRenderer vertexLabelRenderer(Map<String, Node> nodeMap, GraphOptions options) {
        return new DefaultVertexLabelRenderer(options.getFontColor()) {
            private static final long serialVersionUID = 1909972527171078432L;

            public <V> Component getVertexLabelRendererComponent(JComponent vv, Object nodeName, Font font, boolean isSelected, V vertex) {
                super.setForeground(options.getFontColor());

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

                setFont(new Font(options.getFont().getName(), fontStyle, options.getFont().getSize()));
                setIcon(null);
                setBorder(noFocusBorder);
                setValue(nodeName);
                return this;
            }
        };
    }

    protected Transformer<String, String> vertexToolTipTransformer(Map<String, Node> nodeMap) {
        return nodeName -> {
            Node node = nodeMap.get(nodeName);
            return node.name();
        };
    }

    protected Transformer<String, String> edgeToolTipTransformer(Map<String, Edge> edgeMap) {
        return edgeName -> {
            Edge edge = edgeMap.get(edgeName);
            return edge.getTitle();
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

    protected DefaultEdgeLabelRenderer edgeLabelRenderer(GraphOptions options) {
        return new DefaultEdgeLabelRenderer(options.getEdgeColor()) {
            @Override
            public <E> Component getEdgeLabelRendererComponent(JComponent vv, Object nodeName, Font font, boolean isSelected, E edge) {
                super.setForeground(options.getEdgeColor());
                setFont(options.getFont());
                setIcon(null);
                setBorder(noFocusBorder);
                setValue(nodeName);
                return this;
            }
        };
    }

    protected final Transformer<String, Paint> arrowFillPaintTransformer(GraphOptions options) {
        return edgeName -> {
            Edge edge = mEdgeMap.get(edgeName);
            if (edge.isImplements())
                return options.getInterfaceArrowColor();
            else
                return options.getArrowColor();
        };
    }

    public String getElementName() {
        return mElementName;
    }

    public String getFullyQualifiedElementName() {
        return mFullyQualifiedName;
    }

    public GraphZoomScrollPane getGraphPane() {
        return mGraphPane;
    }
}
