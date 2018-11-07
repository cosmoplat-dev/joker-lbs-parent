package com.joker.lbs.worker.thread;

import java.util.concurrent.Callable;

import com.joker.lbs.worker.common.ProcessRequest;
import com.joker.lbs.worker.common.ProcessResult;

public abstract class VCallable implements Callable<ProcessResult> {

	public ProcessRequest request = null;

	public VCallable(ProcessRequest request) {
		this.request = request;
	}

	public ProcessResult call() throws Exception {
		return custom(request);
	}

	public abstract ProcessResult custom(ProcessRequest request);

}
