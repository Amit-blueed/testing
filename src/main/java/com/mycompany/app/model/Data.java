package com.mycompany.app.model;

public class Data {
	private static int nextId=0;
	public int id;
	public String value;
	
	public Data(String value) {
		id = nextId++;
		this.value=value;
		
	}
}
