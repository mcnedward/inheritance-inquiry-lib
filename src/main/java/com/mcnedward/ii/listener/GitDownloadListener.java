package com.mcnedward.ii.listener;

import java.io.File;

/**
 * @author Edward - Jun 25, 2016
 *
 */
public interface GitDownloadListener {
	void onProgressChange(String message, int progress);
	void onDownloadError(String message, Exception exception);
	void finished(File gitFile, String repoName);
}
