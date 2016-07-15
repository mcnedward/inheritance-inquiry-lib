package com.mcnedward.ii.service;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.mcnedward.ii.exception.DownloadException;
import com.mcnedward.ii.listener.GitDownloadListener;

/**
 * Downloads a git repository to a temporary location, which can then be used in the InterfaceInquiry.
 * 
 * @author Edward - Jun 24, 2016
 *
 */
public class GitService {
	protected static final Logger logger = Logger.getLogger(GitService.class);

	/**
	 * Clone a remote git repository. This creates a temporary File directory that holds the contents of that git
	 * repository. The file should be deleted after the JavaProject build is finished.
	 * 
	 * @param remoteUrl
	 *            The URL of the remote repository.
	 * @param repoName
	 *            The name of the repository.
	 * @param username
	 *            The username for authentication.
	 * @param password
	 *            The password for authentication.
	 * @param listener
	 *            The ProjectBuildListener to notify of the progress of the cloning.
	 * @return A File that holds the contents of the remote repository. This File is a git directory (has a .git
	 *         folder). This needs to be deleted once the project has been built.
	 * @throws InvalidRemoteException
	 * @throws GitAPIException
	 * @throws DownloadException
	 */
	public static void downloadFileFromGit(String remoteUrl, String username, String password, GitDownloadListener listener) {
		int slashIndex = remoteUrl.lastIndexOf("/");
		int dotIndex = remoteUrl.lastIndexOf(".");
		String repoName = remoteUrl.substring(slashIndex + 1, dotIndex);
		listener.onProgressChange("Starting download for " + repoName + "...", 0);
		Runnable task = () -> {
			try {
				File tempRepo = File.createTempFile(repoName, "");
				tempRepo.delete();
				tempRepo.deleteOnExit();

				Git gitResult;
				gitResult = Git.cloneRepository().setProgressMonitor(new ProjectBuildMonitor(repoName, listener)).setURI(remoteUrl)
						.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).setDirectory(tempRepo).call();

				// Close everything
				gitResult.getRepository().close();
				gitResult.close();

				listener.finished(tempRepo, repoName);
			} catch (IOException | GitAPIException e) {
				listener.onDownloadError(String.format("Could not download project %s from the remote URL %s.", repoName, remoteUrl), e);
			}

		};

		Thread thread = new Thread(task);
		thread.start();
	}

}

/**
 * Class for monitoring the progress of a git clone, and updating the ProjectBuildListener.
 * 
 * @author Edward - Jun 25, 2016
 *
 */
final class ProjectBuildMonitor implements ProgressMonitor {
	protected static final Logger logger = Logger.getLogger(GitService.class);

	private String mRepoName;
	private GitDownloadListener mListener;
	private int mProgress, mGoal;
	private String mTask;

	public ProjectBuildMonitor(String repoName, GitDownloadListener listener) {
		mRepoName = repoName;
		mListener = listener;
	}

	@Override
	public void beginTask(String task, int goal) {
		mProgress = 0;
		mGoal = goal;
		mTask = task;
	}

	@Override
	public void endTask() {
		mListener.onProgressChange(String.format("Finished download for %s.", mRepoName), 100);
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void start(int arg0) {
		mListener.onProgressChange(String.format("Starting download for %s...", mRepoName), 0);
	}

	@Override
	public void update(int progress) {
		if (mGoal == 0)
			return;
		mProgress += progress;
		int percentageComplete = (int) (((double) mProgress / mGoal) * 100);
		mListener.onProgressChange(String.format("%s: %s - %s", mRepoName, mTask, percentageComplete), percentageComplete);
	}
}
