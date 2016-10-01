package com.mcnedward.ii.builder;

import com.mcnedward.ii.exception.MetricExportException;
import com.mcnedward.ii.listener.GitDownloadListener;
import com.mcnedward.ii.service.IGitService;
import com.mcnedward.ii.service.metric.IMetricService;
import com.mcnedward.ii.service.metric.element.MetricOptions;

/**
 * Created by Edward on 9/25/2016.
 */
public class GitBuilder extends Builder {

    private GitDownloadListener mDownloadListener;
    private IGitService mGitService;
    private String mRemoteUrl;
    private String mUsername;
    private String mPassword;

    public GitBuilder(GitDownloadListener listener) {
        super();
        mDownloadListener = listener;
    }

    public GitBuilder setup(IGitService service, String remoteUrl, String username, String password) {
        mGitService = service;
        mRemoteUrl = remoteUrl;
        mUsername = username;
        mPassword = password;
        return this;
    }

    @Override
    protected Runnable buildTask() {
        if (mGitService == null || mRemoteUrl == null || mUsername == null || mPassword == null) {
            throw new IllegalStateException("You need to call setup method first, and be sure to all the options!");
        }
        return () -> {
            try {
                mGitService.downloadFile(mRemoteUrl, mUsername, mPassword, mDownloadListener);
            } catch (Exception e) {
                mDownloadListener.onBuildError("Something went wrong when exporting the metrics.", e);
            } finally {
                reset();
            }
        };
    }

    @Override
    protected void reset() {
        mGitService = null;
        mRemoteUrl = null;
        mUsername = null;
        mPassword = null;
    }
}
