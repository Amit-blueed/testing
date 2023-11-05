package com.mycompany.app;

import com.mycompany.app.messaging.dataProducer;
import com.mycompany.app.model.Data;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	dataProducer.initialize("tcp://127.0.0.1:61616", "data");
    	Data d = new Data("some value");
        System.out.println( "Hello World!" );
        if(dataProducer.inError()) {
        	System.out.println("check");
        	System.out.println(dataProducer.error());
        }else {
        	dataProducer.add(d);
        }
        dataProducer.terminate();
    }
}
