package com.arsmith.iot.broker;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MessageTuple {
	private MqttMessage message;
	private String topic;
	
	public MessageTuple(MqttMessage message, String topic) {
		this.message=message;
		this.topic=topic;
	}
	
	public MessageTuple() {
		this.message=null;
		this.topic = null;
	}

	public MqttMessage getMessage() {
		return message;
	}
	public void setMessage(MqttMessage message) {
		this.message = message;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
}
