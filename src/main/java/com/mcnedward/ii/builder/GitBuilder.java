package com.mcnedward.ii.builder;

import com.mcnedward.ii.listener.GitDownloadListener;
import com.mcnedward.ii.service.IGitService;

import java.util.Arrays;

/**
 * Created by Edward on 9/25/2016.
 */
public class GitBuilder extends Builder {

    private GitDownloadListener mDownloadListener;
    private IGitService mGitService;
    private String mRemoteUrl;
    private String mUsername;
    private char[] mPassword;
    private boolean mIsToken;

    public GitBuilder(GitDownloadListener listener) {
        super();
        mDownloadListener = listener;
    }

    public GitBuilder setup(IGitService service, String remoteUrl, String username, char[] password) {
        mGitService = service;
        mRemoteUrl = remoteUrl;
        mUsername = username;
        mPassword = password;
        mIsToken = false;
        return this;
    }

    public GitBuilder setup(IGitService service, String remoteUrl, char[] token) {
        mGitService = service;
        mRemoteUrl = remoteUrl;
        mPassword = token;
        mIsToken = true;
        return this;
    }

    @Override
    protected Runnable buildTask() {
        if (mGitService == null || mRemoteUrl == null) {
            throw new IllegalStateException("You need to call setup method first!");
        }
        if (mIsToken) {
            if (mPassword == null || mPassword.length == 0) {
                throw new IllegalStateException("You need to provide a token!");
            }
            if (mUsername != null) {
                throw new IllegalStateException("You can't have a username if using a token. Make sure to call the correct setup method.");
            }

            return () -> {
                try {
                    mGitService.downloadFile(mRemoteUrl, mPassword, mDownloadListener);
                } catch (Exception e) {
                    mDownloadListener.onBuildError("Something went wrong when exporting the metrics.", e);
                } finally {
                    reset();
                }
            };
        } else {
            if ((mUsername == null || mUsername.equals("") || mPassword == null || mPassword.length == 0)) {
                throw new IllegalStateException("You need to call setup method first, with both a username and password!");
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
    }

    @Override
    protected void reset() {
        mGitService = null;
        mRemoteUrl = null;
        mUsername = null;
        if (mPassword != null) {
            Arrays.fill(mPassword, (char) 0);
            mPassword = null;
        }
    }
}
