package com.arsmith.iot.broker;

public class MqttSecureSubscriber extends MqttSubscriber {

	public MqttSecureSubscriber(String brokerUrl, String clientId, String topic, MessageQueue queue) {
		super(brokerUrl, clientId, topic, queue);
	}
	
	@Override
	protected void connectToBroker() {
		super.connectToBroker();
	}

}
