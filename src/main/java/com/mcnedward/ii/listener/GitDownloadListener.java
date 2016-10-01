package com.mcnedward.ii.listener;

import java.io.File;

/**
 * @author Edward - Jun 25, 2016
 *
 */
public interface GitDownloadListener extends BuildListener {
	void finished(File gitFile, String repoName);
}
