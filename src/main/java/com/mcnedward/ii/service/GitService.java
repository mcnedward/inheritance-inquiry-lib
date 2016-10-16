package com.mcnedward.ii.service;

import java.io.File;
import java.security.SecureRandom;
import java.util.Arrays;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.mcnedward.ii.listener.GitDownloadListener;
import com.mcnedward.ii.utils.IILogger;
import com.mcnedward.ii.utils.IIUtils;

/**
 * Downloads a git repository to a temporary location, which can then be used in the InterfaceInquiry.
 *
 * @author Edward - Jun 24, 2016
 *
 */
public class GitService implements IGitService {

    private static final SecureRandom random = new SecureRandom();

    @Override
    public void downloadFile(String remoteUrl, char[] token, GitDownloadListener listener) {
        download(remoteUrl, new UsernamePasswordCredentialsProvider(new String(token), ""), token, listener);
    }

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
	public void downloadFile(String remoteUrl, String username, char[] password, GitDownloadListener listener) {
        download(remoteUrl, new UsernamePasswordCredentialsProvider(username, password), password, listener);
    }

    private void download(String remoteUrl, CredentialsProvider credentialsProvider, char[] password, GitDownloadListener listener) {
		String repoName = IIUtils.getGitProjectName(remoteUrl);
        IILogger.notify(listener, "Starting download for " + repoName + "...", 0);

        Git gitResult = null;
        try {
            String tempDirectory = System.getProperty("java.io.tmpdir");
            int n = random.nextInt((999 - 1) + 1) + 1;
            File tempRepo = new File(String.format("%s/%s/%s-%s", tempDirectory, "II", repoName, Integer.toString(n)));

            gitResult = Git.cloneRepository()
                    .setProgressMonitor(new ProjectBuildMonitor(repoName, listener))
                    .setURI(remoteUrl)
                    .setCredentialsProvider(credentialsProvider)
                    .setDirectory(tempRepo).call();

            // Close everything
            gitResult.getRepository().close();
            gitResult.close();

            listener.finished(tempRepo, repoName);
        } catch (JGitInternalException | GitAPIException e) {
            listener.onBuildError(String.format("Could not download project %s from the remote URL %s.", repoName, remoteUrl), e);
        } catch (UnsupportedCredentialItem e) {
            listener.onBuildError("Could not connect to your account. Make sure you are connecting through https and using your username and password, as ssh is not supported right now.", e);
        } finally {
            // Clear the password
            if (password != null) {
                Arrays.fill(password, (char) 0);
                password = null;
            }
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
