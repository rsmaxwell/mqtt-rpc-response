package com.rsmaxwell.mqtt.rpc.response;

import java.net.HttpURLConnection;
import java.util.Map;

import com.rsmaxwell.mqtt.rpc.common.Response;
import com.rsmaxwell.mqtt.rpc.common.Result;

public abstract class RequestHandler {

	public abstract Result handleRequest(Map<String, Object> args) throws Exception;

	public static Result ok() {
		Response response = new Response();
		response.put("code", HttpURLConnection.HTTP_OK);
		return new Result(response, false);
	}

	public static Result success(Object value) {
		Response response = new Response();
		response.put("code", HttpURLConnection.HTTP_OK);
		response.put("result", value);
		return new Result(response, false);
	}

	public static Result quit() {
		Response response = new Response();
		response.put("code", HttpURLConnection.HTTP_OK);
		return new Result(response, true);
	}

	public static Result badRequest(String message) {
		Response response = new Response();
		response.put("code", HttpURLConnection.HTTP_BAD_REQUEST);
		response.put("message", message);
		return new Result(response, false);
	}

	public static Result internalError(String message) {
		Response response = new Response();
		response.put("code", HttpURLConnection.HTTP_INTERNAL_ERROR);
		response.put("message", message);
		return new Result(response, false);
	}
}
