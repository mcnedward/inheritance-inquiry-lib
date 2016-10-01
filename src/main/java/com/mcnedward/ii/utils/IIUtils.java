package com.mcnedward.ii.utils;

/**
 * Created by Edward on 10/1/2016.
 */
public class IIUtils {

    public static String getGitProjectName(String remoteUrl) {
        String url = remoteUrl.replace("\\", "/");
        int slashIndex = url.lastIndexOf("/");
        int dotIndex = url.lastIndexOf(".");
        return url.substring(slashIndex + 1, dotIndex);
    }

}
