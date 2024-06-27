package com.rsmaxwell.mqtt.rpc.response;

import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.mqttv5.common.MqttSubscription;

import com.rsmaxwell.mqtt.rpc.response.handlers.Calculator;
import com.rsmaxwell.mqtt.rpc.response.handlers.GetPages;
import com.rsmaxwell.mqtt.rpc.response.handlers.Quit;
import com.rsmaxwell.mqtt.rpc.response.handlers.RequestHandler;

public class Responder {

	static String clientID_responder = "responder";
	static String clientID_subscriber = "listener";
	static String requestTopic = "request";

	static int qos = 0;

	static MessageHandler messageHandler;

	static {
		HashMap<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();
		handlers.put("calculator", new Calculator());
		handlers.put("getPages", new GetPages());
		handlers.put("quit", new Quit());

		messageHandler = new MessageHandler(handlers);
	}

	public static void main(String[] args) throws Exception {

		Options options = new Options();
		Option serverOption = createOption("s", "server", "mqtt server", "URL of MQTT server", false);
		Option usernameOption = createOption("u", "username", "Username", "Username for the MQTT server", true);
		Option passwordOption = createOption("p", "password", "Password", "Password for the MQTT server", true);
		options.addOption(serverOption).addOption(usernameOption).addOption(passwordOption);

		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = commandLineParser.parse(options, args);
		String server = commandLine.hasOption("h") ? commandLine.getOptionValue(serverOption) : "tcp://127.0.0.1:1883";
		String username = commandLine.getOptionValue(usernameOption);
		String password = commandLine.getOptionValue(passwordOption);

		MqttClientPersistence persistence = new MqttDefaultFilePersistence();

		MqttAsyncClient client_responder = new MqttAsyncClient(server, clientID_responder, persistence);
		MqttAsyncClient client_subscriber = new MqttAsyncClient(server, clientID_subscriber, persistence);

		messageHandler.setClient(client_responder);
		client_subscriber.setCallback(messageHandler);

		System.out.printf(String.format("Connecting to broker: %s as '%s'\n", server, clientID_responder));
		MqttConnectionOptions connOpts_responder = new MqttConnectionOptions();
		connOpts_responder.setUserName(username);
		connOpts_responder.setPassword(password.getBytes());
		connOpts_responder.setCleanStart(true);
		client_responder.connect(connOpts_responder).waitForCompletion();

		System.out.printf(String.format("Connecting to broker: %s as '%s'\n", server, clientID_subscriber));
		MqttConnectionOptions connOpts_subscriber = new MqttConnectionOptions();
		connOpts_subscriber.setUserName(username);
		connOpts_subscriber.setPassword(password.getBytes());
		connOpts_subscriber.setCleanStart(true);
		client_subscriber.connect(connOpts_subscriber).waitForCompletion();

		System.out.printf(String.format("subscribing to: %s\n", requestTopic));
		MqttSubscription subscription = new MqttSubscription(requestTopic);
		client_subscriber.subscribe(subscription).waitForCompletion();

		// Wait till quit request received
		messageHandler.waitForCompletion();

		System.out.println("disconnect");
		client_responder.disconnect().waitForCompletion();
		client_subscriber.disconnect().waitForCompletion();

		System.out.println("exiting");
	}

	static Option createOption(String shortName, String longName, String argName, String description, boolean required) {
		return Option.builder(shortName).longOpt(longName).argName(argName).desc(description).hasArg().required(required).build();
	}
}
