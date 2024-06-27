package com.rsmaxwell.mqtt.rpc.response.handlers;

import java.util.Map;

import com.rsmaxwell.mqtt.rpc.common.Response;

public class Quit extends RequestHandler {

	public Response handleRequest(Map<String, Object> args) throws Exception {
		System.out.println("quit.handleRequest");
		return quit();
	}
}
