package com.rsmaxwell.mqtt.rpc.utilities;

public class BadRequest extends StatusException {

	public BadRequest(int code) {
		super(code);
	}

	public BadRequest(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BadRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public BadRequest(String message) {
		super(message);
	}

	public BadRequest(Throwable cause) {
		super(cause);
	}
}
