package com.mcnedward.ii.service;

import java.io.File;
import java.io.IOException;

import com.mcnedward.ii.utils.IIUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.ChainingCredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.mcnedward.ii.listener.GitDownloadListener;

import static java.io.File.createTempFile;

/**
 * Downloads a git repository to a temporary location, which can then be used in the InterfaceInquiry.
 *
 * @author Edward - Jun 24, 2016
 *
 */
public class GitService implements IGitService {
	/**
	 * Clone a remote git repository. This creates a temporary File directory that holds the contents of that git
	 * repository. The file should be deleted after the JavaProject build is finished.
	 *
	 * @param remoteUrl
	 *            The URL of the remote repository.
	 * @param username
	 *            The username for authentication.
	 * @param password
	 *            The password for authentication.
	 * @param listener
	 *            The SolutionBuildListener to notify of the progress of the cloning.
	 */
	@Override
	public void downloadFile(String remoteUrl, String username, String password, GitDownloadListener listener) {
		String repoName = IIUtils.getGitProjectName(remoteUrl);
		listener.onProgressChange("Starting download for " + repoName + "...", 0);
        Git gitResult = null;
        try {
            File tempRepo = createTempFile(repoName, "");
            tempRepo.delete();
            tempRepo.deleteOnExit();

            gitResult = Git.cloneRepository()
                    .setProgressMonitor(new ProjectBuildMonitor(repoName, listener))
                    .setURI(remoteUrl)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                    .setDirectory(tempRepo).call();

            // Close everything
            gitResult.getRepository().close();
            gitResult.close();

            listener.finished(tempRepo, repoName);
        } catch (IOException | GitAPIException e) {
            listener.onBuildError(String.format("Could not download project %s from the remote URL %s.", repoName, remoteUrl), e);
        } catch (UnsupportedCredentialItem e) {
            listener.onBuildError("Could not connect to your account. Make sure you are connecting through https and using your username and password, as ssh is not supported right now.", e);
        } finally {
            if (gitResult != null) {
                if (gitResult.getRepository() != null)
                    gitResult.getRepository().close();
                gitResult.close();
            }
        }
	}

}

/**
 * Class for monitoring the progress of a git clone, and updating the SolutionBuildListener.
 *
 * @author Edward - Jun 25, 2016
 *
 */
final class ProjectBuildMonitor implements ProgressMonitor {

	private String mRepoName;
	private GitDownloadListener mListener;
	private int mProgress, mGoal;
	private String mTask;

	ProjectBuildMonitor(String repoName, GitDownloadListener listener) {
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
