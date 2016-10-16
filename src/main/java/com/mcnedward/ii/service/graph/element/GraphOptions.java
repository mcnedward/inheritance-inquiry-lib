package com.mcnedward.ii.service.graph.element;

import com.mcnedward.ii.element.JavaSolution;

import java.awt.*;
import java.io.File;
import java.util.Collection;

/**
 * Created by Edward on 9/26/2016.
 */
public class GraphOptions {

    // Distance between graphs
    public static final int DEFAULT_X_DIST = 250;
    public static final int DEFAULT_Y_DIST = 100;
    public static final int DEFAULT_FONT_SIZE = 18;
    public static final Color DEFAULT_LABEL_COLOR = new Color(80, 95, 110);
    public static final Color DEFAULT_FONT_COLOR = Color.BLACK;
    public static final Color DEFAULT_ARROW_COLOR = Color.BLACK;
    public static final Color DEFAULT_EDGE_COLOR = Color.BLACK;
    public static final Color DEFAULT_INTERFACE_LABEL_COLOR = new Color(140, 160, 200);
    public static final Color DEFAULT_INTERFACE_ARROW_COLOR = Color.WHITE;
    public static final Color DEFAULT_INTERFACE_EDGE_COLOR = Color.WHITE;
    public static final GraphShape DEFAULT_GRAPH_SHAPE = GraphShape.RECT;

    private JavaSolution mSolution;
    private Collection<String> mFullyQualifiedNames;
    private Integer mLimit;
    private Integer mWidth;
    private Integer mHeight;
    private Integer mXDist;
    private Integer mYDist;
    private Font mFont;
    private Color mLabelColor, mFontColor, mArrowColor, mEdgeColor, mInterfaceLabelColor, mInterfaceArrowColor, mInterfaceEdgeColor;
    private GraphShape mGraphShape;
    private boolean mUseFullName, mUsePackages, mShowEdgeLabel;
    private File mDirectory;
    private String mProjectName;

    /**
     * The default options to use when building graphs.
     * @param solution The {@link JavaSolution}
     */
    public GraphOptions(JavaSolution solution) {
        this(DEFAULT_X_DIST, DEFAULT_Y_DIST, DEFAULT_FONT_SIZE, DEFAULT_FONT_COLOR, DEFAULT_LABEL_COLOR, DEFAULT_ARROW_COLOR, DEFAULT_EDGE_COLOR, DEFAULT_GRAPH_SHAPE);
        mSolution = solution;
    }

    public GraphOptions(Integer xDist, Integer yDist, Integer fontSize, Color fontColor, Color labelColor, Color arrowColor, Color edgeColor, GraphShape graphShape) {
        mXDist = xDist;
        mYDist = yDist;
        mFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        mFontColor = fontColor == null ? DEFAULT_FONT_COLOR : fontColor;
        mLabelColor = labelColor == null ? DEFAULT_LABEL_COLOR : labelColor;
        mArrowColor = arrowColor == null ? DEFAULT_ARROW_COLOR : arrowColor;
        mEdgeColor = edgeColor == null ? DEFAULT_EDGE_COLOR : edgeColor;
        mInterfaceLabelColor = DEFAULT_INTERFACE_LABEL_COLOR;
        mInterfaceArrowColor = DEFAULT_INTERFACE_ARROW_COLOR;
        mInterfaceEdgeColor = DEFAULT_INTERFACE_EDGE_COLOR;
        mGraphShape = graphShape;
    }

    /**
     * The options to use when exporting graphs.
     *
     * @param directory   The location to save the graphs to.
     * @param projectName The name of the project.
     * @param usePackages True if you want the graphs to be saved using their package structure, false if they should
     *                    all be in one directory.
     */
    public GraphOptions(File directory, String projectName, boolean usePackages) {
        mDirectory = directory;
        mProjectName = projectName;
        mUsePackages = usePackages;
    }

    public JavaSolution getSolution() {
        return mSolution;
    }

    public void setSolution(JavaSolution solution) {
        mSolution = solution;
    }

    public Collection<String> getFullyQualifiedNames() {
        return mFullyQualifiedNames;
    }

    public void setFullyQualifiedNames(Collection<String> fullyQualifiedNames) {
        mFullyQualifiedNames = fullyQualifiedNames;
    }

    public Integer getLimit() {
        return mLimit;
    }

    public void setLimit(Integer limit) {
        mLimit = limit;
    }

    public Integer getWidth() {
        return mWidth;
    }

    public void setWidth(Integer width) {
        mWidth = width;
    }

    public Integer getHeight() {
        return mHeight;
    }

    public void setHeight(Integer height) {
        mHeight = height;
    }

    public Integer getXDist() {
        return mXDist;
    }

    public void setXDist(Integer xDist) {
        mXDist = xDist;
    }

    public Integer getYDist() {
        return mYDist;
    }

    public void setYDist(Integer yDist) {
        mYDist = yDist;
    }

    public void setFontSize(Integer fontSize) {
        getFont().deriveFont(fontSize);
    }

    public Font getFont() {
        return mFont;
    }

    public void setFont(Font font) {
        mFont = font;
    }

    public Color getLabelColor() {
        return mLabelColor;
    }

    public void setLabelColor(Color color) {
        mLabelColor = color;
    }

    public Color getFontColor() {
        return mFontColor;
    }

    public void setFontColor(Color mFontColor) {
        this.mFontColor = mFontColor;
    }

    public Color getArrowColor() {
        return mArrowColor;
    }

    public void setArrowColor(Color mArrowColor) {
        this.mArrowColor = mArrowColor;
    }

    public Color getEdgeColor() {
        return mEdgeColor;
    }

    public void setEdgeColor(Color mEdgeColor) {
        this.mEdgeColor = mEdgeColor;
    }

    public Color getInterfaceLabelColor() {
        return mInterfaceLabelColor;
    }

    public void setInterfaceLabelColor(Color interfaceArrowColor) {
        mInterfaceLabelColor = interfaceArrowColor;
    }

    public Color getInterfaceArrowColor() {
        return mInterfaceArrowColor;
    }

    public void setInterfaceArrowColor(Color interfaceArrowColor) {
        mInterfaceArrowColor = interfaceArrowColor;
    }

    public Color getInterfaceEdgeColor() {
        return mInterfaceEdgeColor;
    }

    public void setInterfaceEdgeColor(Color interfaceEdgeColor) {
        mInterfaceEdgeColor = interfaceEdgeColor;
    }

    public GraphShape getGraphShape() {
        return mGraphShape;
    }

    public void setGraphShape(GraphShape graphShape) {
        mGraphShape = graphShape;
    }

    public boolean useFullName() {
        return mUseFullName;
    }

    public void setUseFullName(boolean useFullName) {
        mUseFullName = useFullName;
    }

    public boolean usePackages() {
        return mUsePackages;
    }

    public void setUsePackages(boolean usePackages) {
        mUsePackages = usePackages;
    }

    public boolean showEdgeLabel() {
        return mShowEdgeLabel;
    }

    public void setShowEdgeLabel(boolean showEdgeLabel) {
        mShowEdgeLabel = showEdgeLabel;
    }

    public File getDirectory() {
        return mDirectory;
    }

    public void setDirectory(File directory) {
        mDirectory = directory;
    }

    public String getProjectName() {
        return mProjectName;
    }

    public void setProjectName(String projectName) {
        mProjectName = projectName;
    }

}
