/**
* Class to process the americold data and update the status to inprogress state in americold db
*
* @author Naveen Kumar
*/
package com.aws.americold.updateStatus;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AmericoldUpdateStatusHandler implements RequestHandler<Object, Object> { 
	
	
	private static Statement stmt = null;
	public Object handleRequest(Object input, Context context) {
		System.out.println("AmericoldUpdateStatusHandler update status Start....");
		String dataToprocess = input.toString().trim().replace("{", "").replace("}", "").replace("[", "").replace("]", "");
		String[] processString = null;
		List<String> americoldStringLst=new ArrayList<String>();
		if (null!=input.toString()) {
			processString=dataToprocess.split(",");
		}
		for (String string : processString) {
			if (string.contains("order_Id")) {
				americoldStringLst.add(String.format("'%s'", string.substring(string.indexOf("=")+1,string.length())).toString());
			}
		}
		Object data=updateOrderStatus(americoldStringLst);
	
		System.out.println("AmericoldUpdateStatusHandler update status End....");
		return data;
	}
	public static String updateOrderStatus(List<String> americoldStringLst) {
		try {
			Connection conn = DBconnection.createNewDBconnection();
			
			
			String sql_update = "Update americold set Order_Status='InProgress' where Order_Id in (##)";
			sql_update=sql_update.replace("##", americoldStringLst.toString()).replace("[", "").replace("]", "");
			System.out.println(sql_update);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql_update);
			conn.setAutoCommit(true);
		} catch (Exception e) {
			return "EXCEPTION";
		} finally {
		}
	return "SUCCESS";
	}
	
}
