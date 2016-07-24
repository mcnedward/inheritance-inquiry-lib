package com.mcnedward.ii.tasks;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
	
	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return super.submit(task);
//		Stopwatch stopwatch = new Stopwatch();
//		stopwatch.start();
//		return super.submit(() -> {
//			stopwatch.stop();
//			IILogger.info("Task %s spent %s in queue.", task, stopwatch.toString());
//			return task.call();
//		});
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
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
		return super.invokeAll(tasks, timeout, unit);
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
