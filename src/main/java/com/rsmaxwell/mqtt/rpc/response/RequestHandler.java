package com.rsmaxwell.mqtt.rpc.response;

import java.util.Map;

import com.rsmaxwell.mqtt.rpc.common.Result;

public abstract class RequestHandler {

	public abstract Result handleRequest(Object ctx, Map<String, Object> args) throws Exception;
}
