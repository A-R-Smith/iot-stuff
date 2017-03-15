package com.arsmith.iot.broker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.arsmith.iot.broker.MessageTuple;

public class SimpleMessageQueue implements MessageQueue {
	private static final int QUEUE_SIZE=10000;
	
	private BlockingQueue<MessageTuple> queue;
	
	
	public SimpleMessageQueue () {
		this.queue= new ArrayBlockingQueue<>(QUEUE_SIZE);
	}

	@Override
	public void push(MessageTuple message) {
		message.setTopic("out");
		queue.add(message);	
	}
	

	@Override
	public MessageTuple pull() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
