package com.joker.lbs.worker.thread;

import java.util.Collection;
import java.util.List;

import com.joker.lbs.worker.common.ProcessResult;

public interface VExecutorService {

	List<ProcessResult> orderSolve(Collection<VCallable> solvers);

	void shutdown();
}
