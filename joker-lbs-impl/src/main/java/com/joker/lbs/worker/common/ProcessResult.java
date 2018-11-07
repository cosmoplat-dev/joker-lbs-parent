package com.joker.lbs.worker.common;

import java.io.Serializable;

public class ProcessResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object obj = null;

	public ProcessResult() {
	}

	public ProcessResult(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
}
