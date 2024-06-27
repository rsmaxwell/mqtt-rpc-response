package com.rsmaxwell.mqtt.rpc.response.handlers;

import java.util.Map;

import com.rsmaxwell.mqtt.rpc.common.Response;
import com.rsmaxwell.mqtt.rpc.common.Utilities;

public class Calculator extends RequestHandler {

	@Override
	public Response handleRequest(Map<String, Object> args) throws Exception {
		System.out.println("calculator.handleRequest");

		try {
			String operation = Utilities.getString(args, "operation");
			int param1 = Utilities.getInteger(args, "param1");
			int param2 = Utilities.getInteger(args, "param2");

			int value = 0;

			switch (operation) {
			case "add":
				value = param1 + param2;
			case "mul":
				value = param1 * param2;
			case "div":
				value = param1 / param2;
			case "sub":
				value = param1 - param2;
			}

			return success(value);
		} catch (Exception e) {

			return badRequest(e.getMessage());
		}
	}
}
