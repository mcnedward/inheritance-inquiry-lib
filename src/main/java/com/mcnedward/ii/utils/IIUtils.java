package com.mcnedward.ii.utils;

/**
 * Created by Edward on 9/26/2016.
 */
public class IIUtils {

    public static String getElementNameFromPackage(String fullyQualifiedName) {
        String[] packages = fullyQualifiedName.split("\\.");
        return packages.length > 0 ? packages[packages.length - 1] : fullyQualifiedName;
    }

}
