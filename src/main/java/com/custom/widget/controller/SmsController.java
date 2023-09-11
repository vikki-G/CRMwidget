package com.custom.widget.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.custom.widget.Config;
import com.custom.widget.model.CrmResponse;
import com.custom.widget.services.TokenService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SmsController {

	private static final Logger LOGGER = LogManager.getLogger("Widget");		
	
	@PostMapping("/postSendNotify")
	public Object customWidgetNotificationRequest(@RequestBody String notifyBody)
	{
		String response = null;					
			try
			{			
				JSONObject notifyBodyJson = new JSONObject(notifyBody);  						
				String smsBody = notifyBodyJson.getString("smsRequestBody");  
				String financialID = notifyBodyJson.getString("financialID");  
				response = SendNotification(smsBody,financialID);
				if(response.toString() != null)
				{				
					CrmResponse resp = new CrmResponse();
					resp.status = "Success";
					resp.message= "Notification Sent";	
					return resp;
				}	
				CrmResponse resp = new CrmResponse();
				resp.status = "Failed";
				resp.message= "Notification Not Sent";	
				return resp;
			} 
			catch (Exception e) 
			{
				LOGGER.error(e.toString());
				CrmResponse resp = new CrmResponse();
				resp.status = "Failed";
				resp.message= e.toString();		
				return resp;
			}
	}	
	
	public String SendNotification(String requestBody, String financialID) throws Exception
	{
		String jsonData = "";
		String accessToken = TokenService.authorizationToken("sms");
		String authorizationToken =  "Bearer "+accessToken;
		//LOGGER.info("SendNotification authorizationToken: " + authorizationToken);
		String Xsignatureresponse = SendXsignature(requestBody);
		LOGGER.info("SendNotification Xsignatureresponse: " + Xsignatureresponse);
		try {		
			
			//String endPoint 	= "https://bawabauat.clouduat.emiratesnbd.com/common/utility/v4/notifications/send";			
			String endPoint 	= Config.smsUrl;			
			LOGGER.info("SendNotification invokeAPI -> started, url: " + endPoint);

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
		
			URL urlForGetRequest = new URL(endPoint);		

			if(endPoint != null)
			{
				String readLine = null;	
		        byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
		        int postDataLength = postData.length;
		        
		        LOGGER.info("SendNotification Request Body : " + requestBody);
		        
				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();				
				connection.setDoOutput(true);				
				connection.setInstanceFollowRedirects( false );
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");				
				connection.setRequestProperty("Authorization", authorizationToken);	
				connection.setRequestProperty("Channel-Id", "IVR");			
				connection.setRequestProperty("Financial-Id", financialID);	
				connection.setRequestProperty("Unique-Reference-Code", "sdsd");	
				connection.setRequestProperty("X-Signature", Xsignatureresponse);				
				connection.setRequestProperty("Client-Timestamp", "1527058644945");		
				connection.setUseCaches( false );	
			    connection.setRequestProperty("charset", "utf-8");
			    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));				
			   
				LOGGER.info("SendNotification connection getRequestProperties : "+ connection.getRequestProperties());
				
				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    	
				osw.write(requestBody.toString());				
				osw.flush();
				osw.close();
				os.close();
				
				LOGGER.info("SendNotification header fields..." + connection.getHeaderFields());	
				
				connection.connect();						
				
				LOGGER.info("SendNotification Response code : "+connection.getResponseCode());
				
				if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 210){
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();while((readLine=in.readLine())!=null)
				{
					response.append(readLine);
				}
					in.close();				
					jsonData=response.toString();
					LOGGER.info("SendNotification Response : "+response.toString());
				}
				else
				{
					LOGGER.error("SendNotification error in response");
					LOGGER.error("SendNotification reponse message"+ connection.getResponseMessage());			
				}	
				LOGGER.info("SendNotification invokeAPI -> "+endPoint+", json: "+new String(jsonData));
			}
			return jsonData;
		
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	public String SendXsignature(String requestBody) throws Exception
	{
		String jsonData = "";	
		
		try {
			//String endPoint 	= "https://test-endpoints-commonservices.clouddev.emiratesnbd.com/hash/4cf2df624c853811fcdd7bb1441bec83";		
			String endPoint 	=  Config.smsSignatureUrl;	
			LOGGER.info("SendXsignature invokeAPI -> started, url: " + endPoint);

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
		
			URL urlForGetRequest = new URL(endPoint);		

			if(endPoint != null)
			{
				String readLine = null;	
		        byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
		        int postDataLength = postData.length;
		        
		        LOGGER.info("SendXsignature Request Body : " + requestBody);
		        
				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();				
				connection.setDoOutput(true);				
				connection.setInstanceFollowRedirects( false );
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");			
				connection.setUseCaches(false);	
			    connection.setRequestProperty("charset", "utf-8");
			    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));				
			   
				LOGGER.info("SendXsignature connection getRequestProperties : "+ connection.getRequestProperties());
				
				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    	
				osw.write(requestBody.toString());				
				osw.flush();
				osw.close();
				os.close();
				
				LOGGER.info("SendXsignature header fields..." + connection.getHeaderFields());	
				
				connection.connect();						
				
				LOGGER.info("SendXsignature Response code : "+connection.getResponseCode());
				
				if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 210){
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();while((readLine=in.readLine())!=null)
				{
					response.append(readLine);
				}
					in.close();				
					jsonData=response.toString();
					LOGGER.info("SendXsignature Response : "+response.toString());
				}
				else
				{
					LOGGER.error("SendXsignature error in response");
					LOGGER.error("SendXsignature reponse message"+ connection.getResponseMessage());			
				}	
				LOGGER.info("SendXsignature invokeAPI -> "+endPoint+", json: "+new String(jsonData));
			}
			return jsonData;
		
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	
	
  
}
