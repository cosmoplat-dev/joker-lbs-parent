package com.joker.lbs.worker.thread;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.joker.lbs.worker.common.ProcessResult;

/**
 * google guava澶氱嚎绋嬪苟琛屽鐞嗘暟鎹�
 * 
 * @author Joker
 *
 */
public class GuavaExecutorService implements VExecutorService {
	public static final Integer DEFAULT_THREADNUMS = Runtime.getRuntime().availableProcessors() * 2;
	private static final Logger log = LoggerFactory.getLogger(GuavaExecutorService.class);

	private Integer threadNums = DEFAULT_THREADNUMS;

	private ExecutorService executorService = null;

	public GuavaExecutorService() {
		init();
	}

	public GuavaExecutorService(Integer threadNums) {
		this.threadNums = threadNums;
		init();
	}

	public void init() {
		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(threadNums);
		}

	}

	public void shutdown() {
		if (executorService != null) {
			executorService.shutdown();
			executorService = null;
		}
	}

	/**
	 * 杩斿洖鏈夊簭缁撴灉
	 * 
	 * @param solvers
	 * @return
	 */
	public List<ProcessResult> orderSolve(Collection<VCallable> solvers) {
		List<ProcessResult> results = Lists.newArrayList();

		try {
			init();
			int n = solvers.size();
			List<ListenableFuture<ProcessResult>> listenableFutures = Lists.newArrayListWithCapacity(n);
			ListeningExecutorService es = MoreExecutors.listeningDecorator(executorService);
			for (Callable<ProcessResult> s : solvers) {
				listenableFutures.add(es.submit(s));
			}

			ListenableFuture<List<ProcessResult>> futureResults = Futures.successfulAsList(listenableFutures);
			for (Object obj : futureResults.get()) {
				if (obj != null) {
					results.add((ProcessResult) obj);
				}
			}
		} catch (Exception e) {
			log.error("ExecutorOrderService.orderSolve", e);
		} finally {
			// shutdown();
		}
		return results;
	}

}
