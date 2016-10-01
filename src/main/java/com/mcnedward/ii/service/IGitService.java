package com.mcnedward.ii.service;

import com.mcnedward.ii.listener.GitDownloadListener;

/**
 * Created by Edward on 10/1/2016.
 */
public interface IGitService {

    void downloadFile(String remoteUrl, String username, String password, GitDownloadListener listener);

}
