package com.rsmaxwell.mqtt.rpc.response;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsmaxwell.mqtt.rpc.common.Adapter;
import com.rsmaxwell.mqtt.rpc.common.Request;
import com.rsmaxwell.mqtt.rpc.common.Response;
import com.rsmaxwell.mqtt.rpc.common.Result;

public class MessageHandler extends Adapter implements MqttCallback {

	private static final Logger logger = LogManager.getLogger(MessageHandler.class);

	private MqttAsyncClient client;
	private Object ctx;
	private HashMap<String, RequestHandler> handlers;
	private ObjectMapper mapper = new ObjectMapper();

	private Object keepRunning = new Object();

	public MessageHandler() {
		handlers = new HashMap<String, RequestHandler>();
	}

	public void putHandler(String key, RequestHandler handler) {
		handlers.put(key, handler);
	}

	public void setClient(MqttAsyncClient client) {
		this.client = client;
	}

	public void setContext(Object ctx) {
		this.ctx = ctx;
	}

	public void waitForCompletion() throws InterruptedException {
		synchronized (keepRunning) {
			keepRunning.wait();
		}
	}

	public void messageArrived(String topic, MqttMessage requestMessage) throws Exception {
		logger.info(String.format("Received request: %s", new String(requestMessage.getPayload())));

		MqttProperties requestProperties = requestMessage.getProperties();
		if (requestProperties == null) {
			logger.error("discarding request with no properties");
			return;
		}

		byte[] correlationData = requestProperties.getCorrelationData();
		if (correlationData == null) {
			logger.error("discarding request with no correlationData");
			return;
		}

		String correlID = new String(correlationData);
		logger.debug(String.format("correlationData: %s", correlID));

		String responseTopic = requestProperties.getResponseTopic();
		if (responseTopic == null) {
			logger.error("discarding request with no responseTopic");
			return;
		}

		if (responseTopic.length() <= 0) {
			logger.error("discarding request with empty responseTopic");
			return;
		}

		logger.debug(String.format("responseTopic:   %s", responseTopic));

		Result result = getResult(responseTopic, requestMessage);

		MqttMessage responseMessage = getResponseMessage(requestMessage, result);

		client.publish(responseTopic, responseMessage).waitForCompletion();

		if (result.isQuit()) {
			logger.debug("quitting");
			synchronized (keepRunning) {
				keepRunning.notify();
			}
			return;
		}
	}

	private Result getResult(String responseTopic, MqttMessage requestMessage) {
		Result result = null;

		String payload = new String(requestMessage.getPayload());

		Request request = null;
		try {
			logger.debug("decoding message payload");
			request = mapper.readValue(payload, Request.class);
		} catch (Exception e) {
			return Result.badRequestException(e);
		}

		if (request == null) {
			return Result.badRequest("missing request");
		}

		if (request.getFunction() == null) {
			return Result.badRequest("missing function");
		}

		if (request.getFunction().length() <= 0) {
			return Result.badRequest("empty function");
		}

		RequestHandler handler = handlers.get(request.getFunction());
		if (handler == null) {
			return Result.badRequest(String.format("unexpected function: %s", request.getFunction()));
		}

		try {
			result = handler.handleRequest(ctx, request.getArgs());
		} catch (Exception e) {
			logger.catching(e);
			return Result.badRequestException(e);
		}

		if (result == null) {
			return Result.badRequest("result is null");
		}

		return result;
	}

	private MqttMessage getResponseMessage(MqttMessage requestMessage, Result result) {

		Response response = result.getResponse();
		if (response == null) {
			response = Response.internalError("discarding request because response was null");
		}

		logger.debug("encoding response");
		byte[] body = null;
		try {
			body = mapper.writeValueAsBytes(response);
		} catch (Exception e) {
			logger.catching(e);
			String message = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
			body = message.getBytes();
		}

		if (body == null) {
			String message = "response body is null";
			logger.error(message);
			body = message.getBytes();
		}

		int qos = 0;
		MqttProperties responseProperties = new MqttProperties();
		responseProperties.setCorrelationData(requestMessage.getProperties().getCorrelationData());
		MqttMessage responseMessage = new MqttMessage(body);
		responseMessage.setProperties(responseProperties);
		responseMessage.setQos(qos);

		logger.info(String.format("Sending reply: %s", new String(body)));

		return responseMessage;
	}
}
