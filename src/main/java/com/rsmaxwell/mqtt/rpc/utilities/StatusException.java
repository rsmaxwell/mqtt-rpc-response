package com.rsmaxwell.mqtt.rpc.utilities;

public class StatusException extends Exception {

	private int code;

	public StatusException(int code) {
		super();
		this.code = code;
	}

	public StatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public StatusException(String message, Throwable cause) {
		super(message, cause);
	}

	public StatusException(String message) {
		super(message);
	}

	public StatusException(Throwable cause) {
		super(cause);
	}

	public int getCode() {
		return this.code;
	}
}
