package idv.zero.recipeBoot.controller;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BotController {
//Ngrok

	@Autowired
	private DataSource dataSource;

	@RequestMapping("/AAA")
	public void name(@RequestBody String body, HttpServletResponse res) {
		int i = 0;
		res.setCharacterEncoding("UTF-8");
		ArrayList<String> idList = new ArrayList<String>();
		ArrayList<String> TextList = new ArrayList<String>();
		ArrayList<String> TitleList = new ArrayList<String>();
		String repiceName = null;
		System.out.println("**************");
//		System.out.println(body);
		String RecId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		JSONObject t = new JSONObject();
		StringBuilder MateriaLName = new StringBuilder();
//		JSONArray MateriaLName = new JSONArray();//材料
		StringBuilder StepText = new StringBuilder();
//		JSONArray StepText = new JSONArray();
		JSONObject text = new JSONObject();
		JSONArray fulfillmentMessages = new JSONArray();
		;
		JSONObject AAA = new JSONObject();
		// 接收資料 讀出repiceName
		try {
			JSONObject jObj = new JSONObject(body);
			JSONObject queryResult = jObj.getJSONObject("queryResult");
			JSONObject parameters = queryResult.getJSONObject("parameters");
			repiceName = parameters.getString("repiceName");
			System.out.println("repiceName:" + repiceName);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("**************");
		System.out.println(repiceName);

		// 查詢資料庫
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			String sql = "select  * from `recipe_main` where  RecTitle like '%" + repiceName + "%' ";
			System.out.println(sql);
			rset = stmt.executeQuery(sql);
			while (rset.next()) {
				TextList.add(rset.getString("RecText"));
				idList.add(rset.getString("RecId"));
				TitleList.add(rset.getString("RecTitle"));
			}
			if (idList.size() != 0) {
				int x = (int) (Math.random() * idList.size());
				RecId = idList.get(x);
				String col1 = TitleList.get(x);
				String col2 = TextList.get(x);
				System.out.println(col1 + ":" + col2);

				MateriaLName.append(col1 + "  需要:\n");

				// 查材料
				sql = "select  * from `recipe_material` where  RecId = " + RecId;
				System.out.println(sql);
				rset = stmt.executeQuery(sql);
				while (rset.next()) {
					MateriaLName.append(rset.getString("MateriaLName") + " \n ");
				}
//				查步驟
				sql = "select  * from `recipe_step` where  RecId = " + RecId;
				System.out.println(sql);
				rset = stmt.executeQuery(sql);
				while (rset.next()) {
					i++;
					StepText.append("\n\n步驟" + i + ":" + rset.getString("StepText"));
				}

//				JSONArray MateriaLName = new JSONArray();//材料
//				JSONObject text = new JSONObject();
//				JSONArray fulfillmentMessages=new JSONArray();;
//				JSONObject AAA = new JSONObject();
				JSONArray tmd = new JSONArray();
				tmd.put(MateriaLName);
				text.put("text", tmd);
				t.put("text", text);
				fulfillmentMessages.put(t);
				JSONObject t2 = new JSONObject();
				JSONObject text2 = new JSONObject();
				JSONArray tmd2 = new JSONArray();
				tmd2.put(StepText);
				text2.put("text", tmd2);
				t2.put("text", text2);
				fulfillmentMessages.put(t2);

			} else {

//				JSONArray tmd = new JSONArray();
//				tmd.put("抱歉查不到資料");
//				text.put("text", tmd);
//				t.put("text", text);
//				fulfillmentMessages.put(t);

			}
			AAA.put("fulfillmentMessages", fulfillmentMessages);
			PrintWriter out = res.getWriter();
			out.print(AAA);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			rset.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
