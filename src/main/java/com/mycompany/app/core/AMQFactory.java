package com.mycompany.app.core;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.mycompany.app.Config;


public class AMQFactory implements MessageListener, AutoCloseable {

	private MessageProducer producer;
	private MessageConsumer consumer;
	private Session session;
	private IEventListener listener;
	

    public void init(String brokerURL, String queueName) throws JMSException {
        // Create a connection factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);

        // Create a connection
        Connection connection = connectionFactory.createConnection();

        // Create a session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create a destination
        Destination destination = session.createQueue(queueName);

        // Create a producer
        producer = session.createProducer(destination);
        
        // Create a consumer
        consumer = session.createConsumer(destination);

        // Set the message listener
        consumer.setMessageListener(this);
 
    }
    
    public void registerListener(IEventListener listener) {
    	this.listener = listener;
    }
    
    @Override
    public void onMessage(Message message) {
        // Get the table data from the message body
        try {
			String payload = ((TextMessage) message).getText();
			listener.received(payload);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void postMessage(String payload) throws JMSException {
		// Create a text message
		TextMessage message = session.createTextMessage();
		
		// Set the payload as the message body
		message.setText(payload);
		
		// Send the message
		producer.send(message);
	}

	@Override
	public void close() throws Exception {
		producer.close();
		consumer.close();
		session.close();
	}
  
}