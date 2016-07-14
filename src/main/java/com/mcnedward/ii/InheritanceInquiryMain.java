package com.mcnedward.ii;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mcnedward.ii.element.JavaProject;
import com.mcnedward.ii.element.JavaSystem;
import com.mcnedward.ii.listener.ProjectBuildListener;
import com.mcnedward.ii.listener.SystemBuildListener;
import com.mcnedward.ii.utils.GraphBuilder;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jun 16, 2016
 *
 */
public class InheritanceInquiryMain {

	// Directory paths
	private static final String QUALITUS_CORPUS_SYSTEMS_PATH = "C:/QC/pt1/Systems/";
	private static final String GRAPH_DIRECTORY_PATH = "C:/users/edward/dev/IIGraphs";
	
	// For buildSystem()
	private static final String SYSTEM = "azureus";
	private static final String SYTEM_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + SYSTEM;
	
	// For buildProject()
	private static final String PROJECT_NAME = "azureus";
	private static final String PROJECT_PATH = QUALITUS_CORPUS_SYSTEMS_PATH + "azureus/azureus-2.0.8.2";
	
	private static final int QUEUE_SIZE = 10;
	private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
	
	private static InheritanceInquiryEclipse mInheritanceInquiry;
	private static GraphBuilder mGraphBuilder;
	
	public static void main(String[] args) {
		mInheritanceInquiry = new InheritanceInquiryEclipse();
		mGraphBuilder = new GraphBuilder(GRAPH_DIRECTORY_PATH);
		
		buildSystem();
	}
	
	protected static void buildSystem() {
		final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
		ExecutorService executor = new ThreadPoolExecutor(2, THREAD_POOL_SIZE, 10, TimeUnit.SECONDS, queue);
		
		mInheritanceInquiry.buildSystem(SYTEM_PATH, executor, queue, QUEUE_SIZE, new SystemBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
				IILogger.info("%s - %s", message, progress);
			}

			@Override
			public void onBuildError(String message, Exception exception) {
				IILogger.error(message, exception);
				shutdown(executor);
			}

			@Override
			public void onProjectBuilt(JavaProject project) {
				IILogger.info("Finished building project %s. Now building graphs.", project.toString());
				mGraphBuilder.buildGraphs(project);
			}

			@Override
			public void onAllProjectsBuilt(JavaSystem system) {
				IILogger.info("All projects for system %s have been built! [%s]", system.getName(), system.getProjectBuildTime());
				shutdown(executor);
			}
			
		});
	}
	
	private static void shutdown(ExecutorService executor) {
		// Shutdown the ExecutorService now that all projects are built
		try {
		    IILogger.info("Attempting to shutdown executor...");
		    executor.shutdown();
		    executor.awaitTermination(5, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			IILogger.info("Build tasks were interrupted...");
		}
		finally {
		    if (!executor.isTerminated()) {
		        IILogger.info("Canceling non-finished build tasks...");
		    }
		    executor.shutdownNow();
		    IILogger.info("Shutdown complete.");
		}
		
		System.exit(0);
	}
	
	protected static void buildProject() {
		mInheritanceInquiry.buildProject(PROJECT_PATH, PROJECT_NAME, new ProjectBuildListener() {

			@Override
			public void onProgressChange(String message, int progress) {
				IILogger.info("%s - %s", message, progress);
			}

			@Override
			public void finished(JavaProject project) {
				System.out.println(project);
				System.out.println("Number of classes: " + project.getClasses().size());
				System.out.println("Number of interfaces: " + project.getInterfaces().size());
				System.out.println();
				
				mGraphBuilder.buildGraphs(project);
			}

			@Override
			public void onBuildError(String message, Exception exception) {
				IILogger.error(message, exception);
			}
			
		});
	}

}
