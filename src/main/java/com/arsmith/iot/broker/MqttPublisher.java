package com.arsmith.iot.broker;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.arsmith.iot.broker.MessageQueue;
import com.arsmith.iot.broker.MessageTuple;
import com.arsmith.iot.broker.MqttPublisher;

public class MqttPublisher implements Runnable {
	
	final static Logger logger = Logger.getLogger(MqttPublisher.class);
	
	protected MqttClient mqttClient;
	protected MessageQueue queue;
	
    protected String brokerUrl;
    protected String clientId;
    protected String infoTopic;

    static final boolean CLEAN = true;
    static final int KEEP_ALIVE = 0; //keep alive(in seconds) is 24 hours
    static final int QOS = 0;  

	public MqttPublisher(String brokerUrl, String clientId, String infoTopic, MessageQueue queue) {
    	this.brokerUrl=brokerUrl;
    	this.clientId=clientId;
    	this.queue=queue;
    	this.infoTopic=infoTopic;
    }
    
	public void run() {
		connectToBroker();
		
        //TODO should we keep while "TRUE" here?
        while(true) {
        	MessageTuple tuple = queue.pull();
        	//logger.info("is connected?? " + mqttClient.isConnected());
			try {
				mqttClient.publish(tuple.getTopic(), tuple.getMessage());
				
			} catch (MqttPersistenceException e) {
				logger.error("MqttPersistenceException in MqttPublisher::run publish ", e);
			} catch (MqttException e) {
				if (e.getReasonCode()==MqttException.REASON_CODE_CLIENT_NOT_CONNECTED) {
					logger.error("MqttException, client not connected in MqttPublisher::run publish",e);
					try {
						mqttClient.close();
						while (!mqttClient.isConnected()) {
							logger.info("Attempting to reconnect...");
							connectToBroker();
							if (mqttClient.isConnected()) {
								logger.info("MqttPublisher reconnected");
								MqttMessage msg = new MqttMessage();
								msg.setPayload(("{\"message\":\"client_reconnected\","+
								"\"gatewayID\":\""+clientId+"\","+
								"\"event_ts\":"+System.currentTimeMillis()+"}").getBytes());
								mqttClient.publish(infoTopic, msg);
								//attempt to send message again
								mqttClient.publish(tuple.getTopic(), tuple.getMessage());
							} else {
								try {
									Thread.sleep(4000);
								} catch (InterruptedException e1) {
									logger.error("Interrupted from sleep trying to reconnect in MqttPublisher::run publish ", e1);
								}
							}
						}
					} catch (MqttException e1) {
						logger.error("MqttException trying to reconnect in MqttPublisher::run publish ", e1);
					}
				} else {
					logger.error("MqttException in MqttPublisher::run publish ", e);
				}
			}
        }
	}
	
	protected void connectToBroker() {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            
            MqttConnectOptions conOptions = new MqttConnectOptions();
            //conOptions.
            conOptions.setCleanSession(CLEAN);
            conOptions.setKeepAliveInterval(KEEP_ALIVE);

            mqttClient.connect(conOptions);
               
        } catch (MqttException e) {
        	logger.error("MqttException in MqttPublisher::run subscribe ", e);
        }
	}

}
