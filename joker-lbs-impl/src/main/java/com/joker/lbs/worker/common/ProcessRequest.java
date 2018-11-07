package com.joker.lbs.worker.common;

import java.io.Serializable;

public class ProcessRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private Object obj = null;

	public ProcessRequest() {
	}

	public ProcessRequest(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
}
