package com.mycompany.app.messaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.management.InstanceAlreadyExistsException;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.mycompany.app.model.Data;
//import com.mycompany.app.model.var;

public class dataProducer implements AutoCloseable{
	private String baseUrl;
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Queue queue;
	private MessageProducer producer;
	private boolean inError;
	private String error;
	private static dataProducer instance;
	private dataProducer(String brokerUrl, String queueName) {
		baseUrl = brokerUrl;
		connectionFactory = new ActiveMQConnectionFactory("admin", "admin", brokerUrl);
		try {
			connection = connectionFactory.createConnection();
			connection.start();
		} catch(Exception ex) {
			System.out.println("check inside1");
			inError=true;
			error=ex.getMessage();
			return;
		}
		try {
			session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch(Exception ex) {
			System.out.println("check inside2");
			inError=true;
			error=ex.getMessage();
			return;
		}
		try {
			queue=session.createQueue(queueName);
		} catch(Exception ex) {
			System.out.println("check inside3");
			inError=true;
			error=ex.getMessage();
			return;
		}
		try {
			producer=session.createProducer(queue);
		} catch(Exception ex) {
			System.out.println("check inside4");
			inError=true;
			error=ex.getMessage();
			return;
		}
		inError=false;
		
	}
	
	private void publish(Data data){
    	try {
    		var msg=session.createObjectMessage(data.id); 
    		msg.setIntProperty("item_id", data.id);
    		msg.setStringProperty("value", data.value);
    		producer.send(msg);
    	}catch(Exception ex) {
    		inError=true;
    	}
    }
	
	@Override
	public void close(){
		try {
			producer.close();
			session.close();
			connection.close();
		}catch(Exception Ignored) {}
	}
	
    public static boolean inError() {
    	return instance.inError;
    }
    
    public static String error() {
    	return instance.error;
    }
    
    public static void initialize(String baasUrl, String queueName) {
    	instance = new dataProducer(baasUrl, queueName);
    }
    
    public static void terminate() {
    	instance.close();
    }
    
    public static void add(Data data) {
    	instance.publish(data);
    }
}
