package com.custom.widget.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.custom.widget.Config;
import com.custom.widget.model.POMReverseResponse;
import com.custom.widget.model.pomLeads;
import com.custom.widget.repository.DbConnection;
import com.custom.widget.util.StringUtil;

@Service
public class WidgetServices {
	public static final Logger LOGGER = LogManager.getLogger("ACMSchduler");
	private static final Logger LOGGER2 = LogManager.getLogger("PomReverse");
	private static final Logger LOGGER3 = LogManager.getLogger("ZohoAPIScheduler");

	@Autowired
	DbConnection dbconnection;

	public ArrayList<pomLeads> processPomReverseRequest(String procedureName,String request) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		CallableStatement callableStatement = null;
		connection = this.dbconnection.getConnection(WidgetServices.LOGGER);
		ArrayList<pomLeads> leadlist = new ArrayList<pomLeads>();
		pomLeads pmld = new pomLeads();

		
		try {
			callableStatement = connection.prepareCall("{call " + procedureName + " (?)}");
			callableStatement.setString(1, request.toString());
			
			rs = callableStatement.executeQuery();
			while (rs.next()) {
				pmld.setLeadReferenceId(rs.getString("LeadReference_id"));
				pmld.setLeadStatus(rs.getString("LeadStatus"));
				pmld.setLeadSubStatus(rs.getString("LeadSubStatus"));
				pmld.setRemarks(rs.getString("Remarks"));
				pmld.setReverseApiId(rs.getString("ReverseApiId"));
				//pmld.setHostAddress(rs.getString("ReverseApiId"));
				leadlist.add(pmld);
			}
		} catch (Exception e) {
			WidgetServices.LOGGER2.error("Exception in processPomReverseRequest detail  : " + e.getMessage());
			throw e;
		} finally {
			try {
				this.dbconnection.closeConnection(connection, rs, callableStatement);
			} catch (Exception e2) {
				WidgetServices.LOGGER2.error("Exception to close the database connection : " + e2.getMessage());
			}
		}
		return leadlist;
	}

	public String updatetable(String reverseApiId, POMReverseResponse response) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		Statement callableStatement = null;
		connection = this.dbconnection.getConnection(WidgetServices.LOGGER);

		try {
			callableStatement = connection.createStatement();
			String sql = "update POM_Reverse_Api_details set IsApitriggered='true',APIResponse='" + "Status Code :"
					+ response.getStatusCode() + " | Response :" + response.getResponse() + "' where ReverseApiId="
					+ Integer.parseInt(reverseApiId);
			callableStatement.executeUpdate(sql);
			LOGGER2.info("Response status updated in database for Reverse ApI id..." + reverseApiId);
		} catch (Exception e) {
			WidgetServices.LOGGER2.error("Exception in updatetable : " + e.getMessage());
			throw e;
		} finally {
			try {
				this.dbconnection.closeConnection(connection, rs, null);
			} catch (Exception e2) {
				WidgetServices.LOGGER2
						.error("Exception to close the database connection in updatetable : " + e2.getMessage());
			}
		}
		return null;
	}

	public String deleteAcmAgent() throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		Statement callableStatement = null;
		connection = this.dbconnection.getConnection(WidgetServices.LOGGER);

		try {
			callableStatement = connection.createStatement();
			String sql = "delete from Temp_Store_Acm_Agent_Details;";
			callableStatement.executeUpdate(sql);
			LOGGER.info("Acm Agent Details deleted Successfully....");
		} catch (Exception e) {
			WidgetServices.LOGGER.error("Exception in deleteAcmAgent  : " + e.getMessage());
			throw e;
		} finally {
			try {
				this.dbconnection.closeConnection(connection, rs, null);
			} catch (Exception e2) {
				WidgetServices.LOGGER
						.error("Exception to close the database connection in updatetable : " + e2.getMessage());
			}
		}
		return null;
	}

	public String insertAcmAgent(String userName, String agentName, String attributesFormat, String emailChannel,
			String agentId, String entity) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		Statement callableStatement = null;
		connection = this.dbconnection.getConnection(WidgetServices.LOGGER);

		try {
			callableStatement = connection.createStatement();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			
			java.util.Date date = new java.util.Date();
			String currentDate = formatter.format(date);
		
			String sql = "INSERT INTO Temp_Store_Acm_Agent_Details (agent_name, attributes, email_channel,email_routable_address,agent_voice_id,entity,created_date) "
					+ "VALUES ('" + userName + "', '" + attributesFormat + "', '" + emailChannel + "','" + agentName
					+ "','" + agentId + "','" + entity + "','" + currentDate + "');";
			callableStatement.executeUpdate(sql);
			LOGGER.info("Acm Agent Details inserted Successfully for AgentId.." + agentId);

		} catch (Exception e) {
			WidgetServices.LOGGER.error("Exception in insertAcmAgent  : " + e.getMessage());
			throw e;
		} finally {
			try {
				this.dbconnection.closeConnection(connection, rs, null);
			} catch (Exception e2) {
				WidgetServices.LOGGER
						.error("Exception to close the database connection in updatetable : " + e2.getMessage());
			}
		}
		return null;
	}

	public Object callAcmAgent() {

		Connection connection = null;
		Object response = new Object();
		ResultSet rs = null;
		CallableStatement callStatement = null;
		connection = this.dbconnection.getConnection(WidgetServices.LOGGER);

		try {

			callStatement = connection.prepareCall("{call Sp_insert_Acm_Agent_Details}");
			rs = callStatement.executeQuery();

			while (rs.next()) {

				response = rs.getObject("response");
				LOGGER.info("calling stored procedure-->" + response);
				;
			}

		} catch (Exception e) {

		}
		return response;
	}
	

	
	
	public int checkAcmAgentTemptable() throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		Statement callableStatement = null;
		connection = this.dbconnection.getConnection(WidgetServices.LOGGER);
		
		int numbers=0;	
		String todaydate=StringUtil.getTodayDate();
		
		try {
			LOGGER.info("checkAcmAgentTemptable querycheck....");
			callableStatement = connection.createStatement();
			String sql = "select count(*) AS count from Temp_Store_Acm_Agent_Details where created_date like '%" + todaydate + "%'"; 
			
			LOGGER.info("Temp_Store_Acm_Agent_Details Sql Query: "+ sql);
			rs =callableStatement.executeQuery(sql);
			while(rs.next()){
				numbers = rs.getInt("count");
		    }
			LOGGER.info("checkAcmAgentTemptable queryreturn: "+ numbers);
		} catch (Exception e) {
			WidgetServices.LOGGER.error("Exception in checkAcmAgentTemptable  : " + e.getMessage());
			throw e;
		} finally {
			try {
				this.dbconnection.closeConnection(connection, rs, null);
			} catch (Exception e2) {
				WidgetServices.LOGGER
						.error("Exception to close the database connection in updatetable : " + e2.getMessage());
			}
		}
		
		return numbers;
	}

	public static String callAPITokenGenerate() {
		String url	 = "https://accounts.zoho.com/oauth/v2/token";
		byte[] jsonData = "".getBytes();
		LOGGER2.info("authorizationToken -> started, url: " + url);
		//String response ="";
		URL obj;
		String responsereturn="";
		try
		{
			
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		            public void checkClientTrusted(X509Certificate[] certs, String authType) {
		            }
		            public void checkServerTrusted(X509Certificate[] certs, String authType) {
		            }
		        }
		    };
			
			// Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	 
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	        	public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			
			
			
			
			obj = new URL(url);
			LOGGER3.info("Initiating GET request with : "+ url);
			/*HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			//con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("refresh_token", "1000.bcd548c9583689ab2f72c07c1220164b.dd096a308bfde921995aa0b108006e17");
			con.setRequestProperty("client_id", "1000.3I1CSCID2BGNSVULNZUUVK9NJJBRDN");
			con.setRequestProperty("client_secret", "3eb7fbea46888431fab0618b7540e3d0debfa24fd5");
			con.setRequestProperty("grant_type", "refresh_token");

			con.setRequestMethod("POST"); */
			
			String readLine = null;
			url="https://accounts.zoho.com/oauth/v2/token?refresh_token=1000.4279917df394b4e683736cf4f04ac50a.eb0364cce67c437ef3a4d096ff3c645e&client_id=1000.HI7YAYCE5PXTGLSCH0WVFR981AZLAS&client_secret=2b1667ab7c5c05f30aad6619fea901f988cf69ba7b&grant_type=refresh_token";
			
			System.out.println("URL "+ url);
			URL urlForGetRequest = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();			
			connection.setDoOutput( true );
			connection.setInstanceFollowRedirects( false );
			connection.setRequestMethod( "POST" );
			/*connection.setRequestProperty("Content-Type", "application/json");	
			connection.setRequestProperty("refresh_token", "1000.bcd548c9583689ab2f72c07c1220164b.dd096a308bfde921995aa0b108006e17");
			connection.setRequestProperty("client_id", "1000.3I1CSCID2BGNSVULNZUUVK9NJJBRDN");
			connection.setRequestProperty("client_secret", "3eb7fbea46888431fab0618b7540e3d0debfa24fd5");
			connection.setRequestProperty("grant_type", "refresh_token"); */
			
			/*connection.setRequestProperty("Content-Type","application/json");	
			connection.setRequestProperty("code","1000.f4fe9d8816614b331d57f6df0a76d009.a474e2e2a615e3ef2805d6b0853c1017");
			connection.setRequestProperty("client_id","1000.RI3TYTFA6N08JPQRCVXBPORYA7NL4N");
			connection.setRequestProperty("client_secret","f69ab24eaf6b400c0109e98da88e33bc0d0815d10c");
			connection.setRequestProperty("grant_type","authorization_code"); 
			connection.setRequestProperty("Accept","application/refresh_token"); */
			
			//connection.setRequestProperty("Content-Type","application/json");	
			/*connection.setRequestProperty("refresh_token", "1000.4279917df394b4e683736cf4f04ac50a.eb0364cce67c437ef3a4d096ff3c645e");
			connection.setRequestProperty("client_id", "1000.HI7YAYCE5PXTGLSCH0WVFR981AZLAS");
			connection.setRequestProperty("client_secret", "2b1667ab7c5c05f30aad6619fea901f988cf69ba7b");
			connection.setRequestProperty("grant_type", "refresh_token"); */
			connection.setRequestMethod("POST"); 
			
			
			connection.setUseCaches( false );
			LOGGER2.info("Connection Request Properties  : " + connection.getRequestProperties());
			OutputStream os = connection.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8"); 
			//osw.write(urlParameters.toString());
			osw.flush();
			osw.close();
			os.close();
			connection.connect();
			
			

			int responseCode = connection.getResponseCode();
			LOGGER3.info("GET Response Code : " + responseCode);
			StringBuffer resp = new StringBuffer();
			if (responseCode == HttpURLConnection.HTTP_OK) { //success
				/*BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					resp.append(inputLine);
				}
				in.close(); */
				
				
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();while((readLine=in.readLine())!=null)
				{
					response.append(readLine);
				}in.close();
				
				System.out.println("Response return "+response);
				jsonData= response.toString().getBytes();
				String tokenResponse = new String(jsonData);
				JSONObject JSONObject = new JSONObject(tokenResponse);			
				responsereturn= JSONObject.getString("access_token");
				
				LOGGER3.debug("getAccessToken POST request successfully completed with response code-> " +responseCode);
			}
			else
			{
				responsereturn ="Failed";
				LOGGER3.error("getAccessToken POST request Call failed with response code " + responseCode);
			}
		}
		catch (Exception e)
		{
			responsereturn ="Failed";
			LOGGER3.error("getAccessToken POST request Call failed with error - " + e.getMessage());
			
		}
		LOGGER3.debug("getAccessToken - End");
		return responsereturn;
	}

	public String callAPIgetRecords(String tokenkey) {
		String url	 = "https://people.zoho.com/people/api/forms/employee/getRecords?sIndex=1&limit=200";
		byte[] jsonData = "".getBytes();
		LOGGER2.info("authorizationToken -> started, url: " + url);
		//String response ="";
		URL obj;
		String responsereturn="";
		try
		{
			
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		            public void checkClientTrusted(X509Certificate[] certs, String authType) {
		            }
		            public void checkServerTrusted(X509Certificate[] certs, String authType) {
		            }
		        }
		    };
			
			// Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	 
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	        	public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			

			obj = new URL(url);
			LOGGER3.info("Initiating GET request with : "+ url);			
			String readLine = null;
			
			System.out.println("URL "+ url);
			URL urlForGetRequest = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();			
			connection.setDoOutput( true );
			connection.setInstanceFollowRedirects( false );
			connection.setRequestMethod( "GET" );
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", "Zoho-oauthtoken "+tokenkey);
			
			connection.setUseCaches( false );
			LOGGER2.info("Connection Request Properties  : " + connection.getRequestProperties());
			OutputStream os = connection.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8"); 
			//osw.write(urlParameters.toString());
			osw.flush();
			osw.close();
			os.close();
			connection.connect();
			
			int responseCode = connection.getResponseCode();
			LOGGER3.info("GET Response Code : " + responseCode);
			StringBuffer resp = new StringBuffer();
			if (responseCode == HttpURLConnection.HTTP_OK) { //success				
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();while((readLine=in.readLine())!=null)
				{
					response.append(readLine);
				}in.close();
				
				//System.out.println("Response return "+response);
				jsonData= response.toString().getBytes();
				responsereturn=jsonData.toString();
				
				LOGGER3.debug("getAccessToken POST request successfully completed with response code-> " +responseCode);
			}
			else
			{
				responsereturn ="Failed";
				LOGGER3.error("getAccessToken POST request Call failed with response code " + responseCode);
			}
		}
		catch (Exception e)
		{
			responsereturn ="Failed";
			LOGGER3.error("getAccessToken POST request Call failed with error - " + e.getMessage());
			
		}
		LOGGER3.debug("getAccessToken - End");
		return responsereturn;
		
		
	}
	
}
