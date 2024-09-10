package com.rsmaxwell.mqtt.rpc.utilities;

public class ServerError extends StatusException {

	public ServerError(int code) {
		super(code);
	}

	public ServerError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServerError(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerError(String message) {
		super(message);
	}

	public ServerError(Throwable cause) {
		super(cause);
	}
}
