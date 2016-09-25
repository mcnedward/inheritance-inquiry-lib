package com.mcnedward.ii.service.graph.jung;

import com.mcnedward.ii.service.graph.element.Node;
import com.mcnedward.ii.utils.IILogger;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;
import org.apache.commons.collections15.Transformer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * Created by Edward on 9/25/2016.
 */
public class NocJungGraph extends JungGraph {

    private static final int X_DIST = 175;
    private static final int X_DIST_FULL = 400;

    public NocJungGraph(String element, boolean useFullName, Integer width, Integer height) {
        super(element, width, height, useFullName ? X_DIST_FULL : X_DIST, null);
        IILogger.info("Building NOC JungGraph. Using full name? %s", useFullName);
    }

    @Override
    protected Transformer<String, Shape> vertexShapeTransformer(VisualizationViewer<String, String> vv, Map<String, Node> nodeMap) {
        final FontMetrics fm = vv.getFontMetrics(vv.getFont());
        return vertexName -> {
            Node node = nodeMap.get(vertexName);
            float width = fm.stringWidth(node.name()) * 1.1f;
            float height = fm.getHeight() * 1.25f;
            float x = -(width / 2.0f);
            float y = -(height / 2.0f);
            new VertexShapeFactory<String>().getRectangle(vertexName);
            Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
            return rect;
        };
    }

}
