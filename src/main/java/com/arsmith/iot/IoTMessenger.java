package com.arsmith.iot;

import com.arsmith.iot.broker.MqttPublisher;
import com.arsmith.iot.broker.MqttSecurePublisher;
import com.arsmith.iot.broker.MqttSubscriber;
import com.arsmith.iot.broker.SimpleMessageQueue;

/**
 * Hello world!
 *
 */
public class IoTMessenger 
{
    public static void main( String[] args )
    {
    	if (args.length<2) {
    		System.out.println("please provide hostnames");
    		System.exit(-1);
    	}
        System.out.println( "IoTMessenger starting up" );
        String subHost = args[0];
        String pubHost = args[1];
        
        System.out.println("Subscribing to: " + subHost);
        System.out.println("Publishing to: " + pubHost);
        
        String subClientId = "subber";
        String pubClientId = "pubber";
        String inputTopic = "test";
        String infoTopic = "info";
        
        
		SimpleMessageQueue queue = new SimpleMessageQueue();
	
		MqttSubscriber subscriber = new MqttSubscriber(subHost,
				subClientId, 
				inputTopic, 
				queue);
		
		MqttPublisher publisher = new MqttSecurePublisher(pubHost,
				pubClientId, 
				infoTopic,
				queue);
		
		new Thread(subscriber).start();
		new Thread(publisher).start();
    }
}
