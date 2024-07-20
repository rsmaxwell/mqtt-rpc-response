package com.rsmaxwell.mqtt.rpc.response.handlers;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rsmaxwell.mqtt.rpc.common.Result;
import com.rsmaxwell.mqtt.rpc.response.RequestHandler;

public class GetPagesHandler extends RequestHandler {

	private static final Logger logger = LogManager.getLogger(GetPagesHandler.class);

	public Result handleRequest(Map<String, Object> args) throws Exception {
		logger.traceEntry();
		return Result.success("[ 'one', 'two', 'three' ]");
	}
}
