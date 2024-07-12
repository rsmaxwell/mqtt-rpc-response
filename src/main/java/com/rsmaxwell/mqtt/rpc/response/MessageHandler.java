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

	public void messageArrived(String topic, MqttMessage requestMessage) throws Exception {
		logger.info(String.format("Received request: %s", new String(requestMessage.getPayload())));

		MqttProperties requestProperties = requestMessage.getProperties();
		if (requestProperties == null) {
			logger.error("discarding request with no properties");
			return;
		}

		String responseTopic = requestProperties.getResponseTopic();
		if (responseTopic == null) {
			logger.error("discarding request with no responseTopic");
			return;
		}

		if (responseTopic.length() <= 0) {
			logger.error("discarding request with empty responseTopic");
			return;
		}

		byte[] correlationData = requestProperties.getCorrelationData();
		String correlID;
		if (correlationData == null) {
			correlID = "(null)";
		} else {
			correlID = new String(correlationData);
		}

		logger.debug(String.format("responseTopic:   %s", responseTopic));
		logger.debug(String.format("correlationData: %s", correlID));

		String payload = new String(requestMessage.getPayload());

		Request request = null;
		try {
			logger.debug("decoding message payload");
			request = mapper.readValue(payload, Request.class);
		} catch (Exception e) {
			logger.error("discarding request because message could not be decoded", e);
			return;
		}

		if (request == null) {
			logger.error("discarding request because there was no request");
			return;
		}

		if (request.getFunction() == null) {
			logger.error("discarding request with no function");
			return;
		}

		if (request.getFunction().length() <= 0) {
			logger.error("discarding request with empty function");
			return;
		}

		RequestHandler handler = handlers.get(request.getFunction());
		if (handler == null) {
			logger.error("discarding request with unexpected function");
			return;
		}

		Result result;
		try {
			result = handler.handleRequest(request.getArgs());
		} catch (Exception e) {
			logger.catching(e);
			logger.error("discarding request because the message could not be handled");
			return;
		}

		if (result == null) {
			logger.error("discarding request because result was null");
			return;
		}

		Response response = result.getResponse();
		if (response == null) {
			logger.error("discarding request because response was null");
			return;
		}

		logger.debug("encoding response");
		byte[] bytes = null;
		try {
			bytes = mapper.writeValueAsBytes(response);
		} catch (Exception e) {
			logger.error("discarding request because the response could not be encoded", e);
			return;
		}

		if (bytes == null) {
			logger.error("discarding request because responseAsBytes was null");
			return;
		}

		MqttProperties responseProperties = new MqttProperties();
		responseProperties.setCorrelationData(requestMessage.getProperties().getCorrelationData());

		int qos = 0;
		MqttMessage responseMessage = new MqttMessage(bytes);
		responseMessage.setProperties(responseProperties);
		responseMessage.setQos(qos);

		logger.info(String.format("Sending reply: %s", new String(bytes)));
		client.publish(responseTopic, responseMessage).waitForCompletion();

		if (result.isQuit()) {
			logger.debug("quitting");
			synchronized (keepRunning) {
				keepRunning.notify();
			}
			return;
		}
	}

	public void waitForCompletion() throws InterruptedException {
		synchronized (keepRunning) {
			keepRunning.wait();
		}
	}
}
