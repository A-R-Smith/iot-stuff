package com.arsmith.iot.broker;

import com.arsmith.iot.broker.MessageTuple;

public interface MessageQueue {
	
	public void push(MessageTuple message);
	
	public MessageTuple pull();

}
