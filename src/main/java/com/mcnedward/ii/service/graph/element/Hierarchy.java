package com.mcnedward.ii.service.graph.element;

import com.mcnedward.ii.element.JavaElement;

/**
 * Created by Edward on 9/24/2016.
 */
public abstract class Hierarchy {

    protected String elementName;
    protected String fullElementName;
    protected String path;
    protected boolean isInterface;

    public Hierarchy(JavaElement element) {
        elementName = element.getName();
        fullElementName = element.getFullyQualifiedName();
        isInterface = element.isInterface();
        path = element.getPackageName().replace(".", "/");
    }

    public String getElementName() {
        return elementName;
    }

    public String getFullElementName() {
        return fullElementName;
    }

    public String getPath() {
        return path;
    }

    public boolean isInterface() {
        return isInterface;
    }
}
