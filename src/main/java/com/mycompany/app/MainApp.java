package com.mycompany.app;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import com.mycompany.app.core.AMQFactory;
import com.mycompany.app.core.DBConnHelper;
import com.mycompany.app.core.IEventListener;

public class MainApp implements IEventListener, Runnable {

	public static void main(String[] args) throws SQLException, JMSException {
		MainApp app = new MainApp();
		DBConnHelper dbConn = new DBConnHelper();
		AMQFactory amq = new AMQFactory();
		
		if(args.length == 0) {
			System.out.println("No instance provided");
		}
		else if ("ship".equals(args[0])) {
			System.out.println("Starting as ship instance");
			dbConn.init(Config.DB_URL_1, Config.DB_USER_1, Config.DB_PASSWORD_1);
		}
		else if ("shore".equals(args[0])) {
			System.out.println("Starting as shore instance");
			dbConn.init(Config.DB_URL_2, Config.DB_USER_2, Config.DB_PASSWORD_2);
		}
		
		amq.init(Config.BROKER_URL, Config.DESTINATION_NAME);
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(app, 0, 10, TimeUnit.SECONDS);
	}

	@Override
	public void received(String payload) {
		System.out.println("Received from AMQ: " + payload);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub	
	}
}
