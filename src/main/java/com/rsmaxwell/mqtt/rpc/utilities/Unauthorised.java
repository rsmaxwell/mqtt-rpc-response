package com.rsmaxwell.mqtt.rpc.utilities;

public class Unauthorised extends StatusException {

	public Unauthorised(int code) {
		super(code);
	}

	public Unauthorised(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public Unauthorised(String message, Throwable cause) {
		super(message, cause);
	}

	public Unauthorised(String message) {
		super(message);
	}

	public Unauthorised(Throwable cause) {
		super(cause);
	}
}
