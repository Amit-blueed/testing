package com.mycompany.app.core;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class DBConnHelper implements AutoCloseable {
	
	private Connection dbConnection;

	public void init(String ip, String user, String password) throws SQLException {
		dbConnection = DriverManager.getConnection(ip, user, password);
	}
	
	public String execSelect(String query) throws SQLException {
		Statement statement = dbConnection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);		
		
		return convertRStoStr(resultSet);
	}

	public String convertRStoStr(ResultSet resultSet) throws SQLException {
		ResultSetMetaData md = resultSet.getMetaData();
		int numCols = md.getColumnCount();
		List<String> colNames = IntStream.range(0, numCols)
		  .mapToObj(i -> {
		      try {
		          return md.getColumnName(i + 1);
		      } catch (SQLException e) {
		          e.printStackTrace();
		          return "?";
		      }
		  })
		  .collect(Collectors.toList());

		JSONArray result = new JSONArray();
		while (resultSet.next()) {
		    JSONObject row = new JSONObject();
		    colNames.forEach(cn -> {
		        try {
					row.put(cn, resultSet.getObject(cn));
				} catch (JSONException | SQLException e) {
					e.printStackTrace();
				}
		    });
		    result.put(row);
		}
		return result.toString();
	}
	
	@Override
	public void close() throws Exception {
		dbConnection.close();
	}
}
