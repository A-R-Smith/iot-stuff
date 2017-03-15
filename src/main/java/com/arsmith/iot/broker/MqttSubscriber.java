package com.arsmith.iot.broker;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.arsmith.iot.broker.MessageQueue;
import com.arsmith.iot.broker.MessageTuple;

public class MqttSubscriber implements Runnable,MqttCallback {
	final static Logger logger = Logger.getLogger(MqttSubscriber.class);
	
	protected MqttClient mqttClient;
	protected MemoryPersistence persistence;
    
	protected MessageQueue queue;
	protected String brokerUrl;
	protected String clientId;
	protected String topic;
    
    static final boolean CLEAN = true;
    static final int KEEP_ALIVE = 0; //keep alive(in seconds) is 24 hours
    static final int QOS = 0;  
    
    public MqttSubscriber(String brokerUrl, String clientId, String topic, MessageQueue queue) {
    	this.brokerUrl=brokerUrl;
    	this.clientId=clientId;
    	this.topic=topic;
    	this.queue=queue;
    	persistence= new MemoryPersistence();
    }
    
	@Override
	public void run() {
		connectToBroker();
	}
    public void connectionLost(Throwable thrwbl) {
        logger.error("Connection lost to MqttSubscriber ", thrwbl);
        try {
			mqttClient.close();
			while (!mqttClient.isConnected()) {
				logger.info("Attempting to reconnect...");
				connectToBroker();
				if (mqttClient.isConnected()) {
					logger.info("MqttSubscriber reconnected");
				} else {
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e1) {
						logger.error("Interrupted from sleep trying to reconnect in MqttSubscriber::connectionLost ", e1);
					}
				}
			}
		} catch (MqttException e) {
			logger.error("MQTT exception while attemping to reconnect in MqttSubscriber::connectionLost", e);
		}
    }

    public void messageArrived(String string, MqttMessage message) throws Exception {
    	queue.push(new MessageTuple(message, topic));    
    }

    public void deliveryComplete(IMqttDeliveryToken imdt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected void connectToBroker () {
    	MqttConnectOptions conOptions = new MqttConnectOptions();
        conOptions.setCleanSession(CLEAN);
        conOptions.setKeepAliveInterval(KEEP_ALIVE);
        try {
            mqttClient = new MqttClient(brokerUrl, clientId, persistence);
            mqttClient.setCallback(this);
            mqttClient.connect(conOptions);
        } catch(MqttException e){
        	logger.error("MQTT exception in MqttSubscriber::run connect ", e);
        }
        
        try {
            mqttClient.subscribe(topic, QOS);
        } catch (MqttException e) {
        	logger.error("MQTT exception in MqttSubscriber::run subscribe", e);
        }
    }
}
