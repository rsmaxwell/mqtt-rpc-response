package com.rsmaxwell.mqtt.rpc.response.handlers;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rsmaxwell.mqtt.rpc.common.Response;
import com.rsmaxwell.mqtt.rpc.response.ResponseHandler;

public class Quit extends ResponseHandler {

	private static final Logger logger = LogManager.getLogger(Quit.class);

	public Response handleRequest(Map<String, Object> args) throws Exception {
		logger.info("quit.handleRequest");
		return quit();
	}
}
