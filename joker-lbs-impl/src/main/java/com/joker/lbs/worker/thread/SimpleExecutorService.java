package com.joker.lbs.worker.thread;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.joker.lbs.worker.common.ProcessResult;

/**
 * 绠�鍗曞绾跨▼骞惰澶勭悊鏁版嵁
 * 
 * @author Joker
 *
 */
public class SimpleExecutorService implements VExecutorService {
	public static final Integer DEFAULT_THREADNUMS = Runtime.getRuntime().availableProcessors() * 2;
	private static final Logger log = LoggerFactory.getLogger(SimpleExecutorService.class);

	private Integer threadNums = DEFAULT_THREADNUMS;

	private ExecutorService executorService = null;

	public SimpleExecutorService() {
		init();
	}

	public SimpleExecutorService(Integer threadNums) {
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
			List<Future<ProcessResult>> futures = Lists.newArrayListWithCapacity(n);
			for (Callable<ProcessResult> s : solvers) {
				futures.add(executorService.submit(s));
			}

			for (Future<ProcessResult> future : futures) {
				results.add(future.get());
			}
		} catch (Exception e) {
			log.error("ExecutorOrderService.orderSolve", e);
		} finally {
			// shutdown();
		}
		return results;
	}

	/**
	 * 杩斿洖鏃犲簭缁撴灉
	 * 
	 * @param solvers
	 * @return
	 */
	public List<ProcessResult> noOrderSolve(Collection<VCallable> solvers) {
		List<ProcessResult> results = Lists.newArrayList();

		CompletionService<ProcessResult> ecs = new ExecutorCompletionService<ProcessResult>(executorService);
		int n = solvers.size();

		try {
			for (Callable<ProcessResult> s : solvers) {
				ecs.submit(s);
			}

			for (int i = 0; i < n; ++i) {
				results.add(ecs.take().get());
			}
		} catch (Exception e) {
			log.error("ExecutorOrderService.noOrderSolve", e);
		}
		return results;
	}
}
