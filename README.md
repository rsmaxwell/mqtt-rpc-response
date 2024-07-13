# mqtt-rpc-response

This project implements the responder part of Remote Procedure Call over mqtt (i.e request/response).
This may be useful in the case of a webapp which needs to subscribe to events, and also to handle requests that need a particular  response  

mqtt expects that a mosquitto broker is running to which clients connect and communicate using standard topics

The user of this project will write a Responder program as described below, and will provide a set of request handler classes 
which will be called process requests

It expects that a requests will be generated by matching requester programs.


### Structure

A user written Responder program:

  * loads a map of request handlers keyed on a function string
  * connects to an mqtt broker 
  * sets a MessageHandler as the callback Adapter
  * subscribes to a topic listens to which requests will be published.
  * waits forever

When a request is published to the request topic, Messagehandler.messageArrived is called which:

  * checks the message has a response topic set in its message properties
  * parses the payload into into a Request 
  * calls the handler matching the Request.function
  * parses the response returned by the handler into a Response 
  * publishes the response to the response topic    
  
A handler implements the handleRequest method which is given an argument of a map of key/value pairs  

  * gets arguments out of the map
  * processes the arguments in some way to generate a response
  * returns a response which includes:
    - error code (borrowed from http status values)
    - error message (optional)
    - response to the request

### Example

An example of the using mqtt-rpc to make a request is: [CalculatorRequest](https://github.com/rsmaxwell/mqtt-rpc-request/blob/main/src/test/java/com/rsmaxwell/mqtt/rpc/request/CalculatorRequest.java)


  