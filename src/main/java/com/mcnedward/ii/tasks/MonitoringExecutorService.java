package com.mcnedward.ii.tasks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.mcnedward.ii.utils.IILogger;

/**
 * @author Edward - Jul 15, 2016
 *
 */
public class MonitoringExecutorService extends ThreadPoolExecutor implements ExecutorService {

	public MonitoringExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
			ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}
	
	public Future<?> submit(ProjectBuildTask task) {
		final Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		return super.submit(() -> {
			stopwatch.stop();
			IILogger.info("%s spent %s in queue.", task, stopwatch.toString());
			task.run();
			return null;
		});
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		return super.submit(() -> {
			stopwatch.stop();
			IILogger.info("Task %s spent %s in queue.", task, stopwatch.toString());
			return task.call();
		});
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return submit(() -> {
			task.run();
			return result;
		});
	}

	@Override
	public Future<?> submit(Runnable task) {
		return submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                task.run();
                return null;
            }
        });
	}
	
	@Override
	 protected void afterExecute(Runnable r, Throwable t) {
	      super.afterExecute(r, t);
	      if (t == null && r instanceof Future<?>) {
	        try {
	          Future<?> future = (Future<?>) r;
	          if (future.isDone()) {
	            future.get();
	          }
	        } catch (CancellationException ce) {
	            t = ce;
	        } catch (ExecutionException ee) {
	            t = ee.getCause();
	        } catch (InterruptedException ie) {
	            Thread.currentThread().interrupt(); // ignore/reset
	        }
	      }
	      if (t != null) {
	            IILogger.error("There was a problem inside the running task " + r + "...", t);
	      }
	 }

}
