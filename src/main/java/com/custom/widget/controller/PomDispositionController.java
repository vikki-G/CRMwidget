package com.custom.widget.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custom.widget.Config;
import com.custom.widget.model.CrmResponse;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PomDispositionController {

	private static final Logger LOGGER = LogManager.getLogger("POM");		
		
	@GetMapping("/getPomDetails")
	public Object getPomDisposition()
	{
		String pomResponse = null;
			try
			{	
				pomResponse = getPomDetails();	
				if(pomResponse.toString() != null)
				{		
					return pomResponse.toString();
				}
				CrmResponse resp = new CrmResponse();
				resp.status = "Failed";
				resp.message= pomResponse.toString();	
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
	
	public String getPomDetails() throws Exception
	{
		String jsonData = "";	
		try {		
			String endPoint 	= Config.pomDisUrl ;	
			LOGGER.info("getPomDetails invokeAPI -> started, url: " + endPoint);

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
		        
				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();	
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", "application/json");
				//connection.setRequestProperty("Accept", "application/json");		
				connection.setRequestProperty("X-Requested-With", "rest");		
				connection.setRequestProperty("Authorization", getAuthorizationToken());						
			   
				LOGGER.info("getPomData connection getRequestProperties : "+ connection.getRequestProperties());		
				LOGGER.info("getPomData header fields..." + connection.getHeaderFields());	
				
				connection.connect();						
				
				LOGGER.info("getPomDetails Response code : "+connection.getResponseCode());
				
				if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 210){
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						StringBuffer response = new StringBuffer();while((readLine=in.readLine())!=null)
						{
							response.append(readLine);
						}
						in.close();				
						jsonData=response.toString();
						LOGGER.info("getPomDetails Response : "+response.toString());
				}
				else
					{
						LOGGER.error("getPomDetails error in response");
						LOGGER.error("getPomDetails reponse message"+ connection.getResponseMessage());	
						jsonData="error";
					}	
				LOGGER.info("getPomDetails invokeAPI -> "+endPoint+", json: "+new String(jsonData));
			}
			return jsonData;
		
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	private String getAuthorizationToken() {		
		String encodeBytes = Base64.getEncoder().encodeToString((Config.pomDisUsername+":"+Config.pomDisPassword).getBytes());
		LOGGER.info("getPomDetails Basic Authorization..."+"Basic " + encodeBytes);
		return "Basic " + encodeBytes;
	}
	
	
	
  
}
