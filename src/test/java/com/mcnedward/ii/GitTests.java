package com.mcnedward.ii;

import com.mcnedward.ii.utils.IIUtils;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by Edward on 10/1/2016.
 */
public class GitTests {

    @Test
    public void testRemoteUrl() {
        String remoteUrl = "https://www.remote.com/mcnedward/inheritance.git";
        String repoName = IIUtils.getGitProjectName(remoteUrl);
        Assert.assertEquals(repoName, "inheritance");
    }

    @Test
    public void testRemoteUrlSwitchesSlash() {
        String remoteUrl = "https:\\\\www.remote.com\\mcnedward\\inheritance.git";
        String repoName = IIUtils.getGitProjectName(remoteUrl);
        Assert.assertEquals(repoName, "inheritance");
    }

}
