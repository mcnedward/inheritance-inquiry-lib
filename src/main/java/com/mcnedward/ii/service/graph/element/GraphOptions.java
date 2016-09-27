package com.mcnedward.ii.service.graph.element;

import com.mcnedward.ii.element.JavaSolution;

import java.awt.*;
import java.util.Collection;

/**
 * Created by Edward on 9/26/2016.
 */
public class GraphOptions {

    // Distance between graphs
    public static final int DEFAULT_X_DIST = 250;
    public static final int DEFAULT_Y_DIST = 100;
    public static final int DEFAULT_FONT_SIZE = 18;

    private JavaSolution mSolution;
    private Collection<String> mFullyQualifiedNames;
    private Integer mLimit;
    private Integer mWidth;
    private Integer mHeight;
    private Integer mXDist;
    private Integer mYDist;
    private Font mFont;
    private boolean mUseFullName;
    private Color mVertexFillPaint;

    public GraphOptions() {
        this(DEFAULT_X_DIST, DEFAULT_Y_DIST, DEFAULT_FONT_SIZE);
    }

    public GraphOptions(Integer xDist, Integer yDist, Integer fontSize) {
        mXDist = xDist;
        mYDist = yDist;
        mFont = new Font("Segoe UI", Font.PLAIN, fontSize);
    }

    public GraphOptions(JavaSolution solution, Collection<String> fullyQualifiedNames, Integer xDist, Integer yDist, Integer fontSize, boolean useFullName) {
        this(xDist, yDist, DEFAULT_FONT_SIZE);
        mSolution = solution;
        mFullyQualifiedNames = fullyQualifiedNames;
        mXDist = xDist;
        mYDist = yDist;
        mUseFullName = useFullName;
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

    public boolean useFullName() {
        return mUseFullName;
    }

    public void setUseFullName(boolean useFullName) {
        mUseFullName = useFullName;
    }

    public Color getVertexFillPaint() {
        return mVertexFillPaint;
    }

    public void setVertexFillPaint(Color color) {
        mVertexFillPaint = color;
    }

}
