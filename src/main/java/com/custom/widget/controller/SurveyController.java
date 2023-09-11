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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.custom.widget.Config;
import com.custom.widget.model.CrmResponse;
import com.custom.widget.services.TokenService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SurveyController {

	private static final Logger LOGGER = LogManager.getLogger("Widget");		
	
	@PostMapping("/postSurvey")
	public Object SendSurveyRequest(@RequestBody String surveyBody)
	{
		String response = null;					
			try
			{					
				response = SendSurveyDetails(surveyBody);
				if(response.toString() != null)
				{				
					CrmResponse resp = new CrmResponse();
					resp.status = "Success";
					resp.message= "Survey Sent";	
					return resp;
				}	
				CrmResponse resp = new CrmResponse();
				resp.status = "Failed";
				resp.message= "Survey Not Sent";	
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
	
	public String SendSurveyDetails(String requestBody) throws Exception
	{
		String jsonData = "";
		String accessToken = TokenService.authorizationToken("sms");
		String authorizationToken =  "Bearer "+accessToken;
		//LOGGER.info("SendSurveyDetails authorizationToken: " + authorizationToken);
		String Xsignatureresponse = SendSurveyXsignature(requestBody);
		LOGGER.info("SendSurveyDetails Xsignatureresponse: " + Xsignatureresponse);
		try {
				
			//String endPoint 	= "https://bawabauat.clouduat.emiratesnbd.com/common/contact-center-operations/v1/voice/contact-lists/41/contacts";		
			String endPoint 	= Config.surveyUrl;	
			LOGGER.info("SendSurveyDetails invokeAPI -> started, url: " + endPoint);

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
		        
		        LOGGER.info("SendSurveyDetails Request Body : " + requestBody);
		        
				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();				
				connection.setDoOutput(true);				
				connection.setInstanceFollowRedirects( false );
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");				
				connection.setRequestProperty("Authorization", authorizationToken);	
				connection.setRequestProperty("Channel-Id", "OMN");			
				connection.setRequestProperty("Financial-Id", "EBI");	
				connection.setRequestProperty("Unique-Reference-Code", "12345");	
				connection.setRequestProperty("X-Signature", Xsignatureresponse);				
				connection.setRequestProperty("Client-Timestamp", "1527058644945");	
				connection.setRequestProperty("Paging-Info", "start-index=0;no-of-records=10");			
				connection.setRequestProperty("Accept-Language", "en");				
				connection.setUseCaches(false);	
			    connection.setRequestProperty("charset", "utf-8");
			    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));				
			   
				LOGGER.info("SendSurveyDetails connection getRequestProperties : "+ connection.getRequestProperties());
				
				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    	
				osw.write(requestBody.toString());				
				osw.flush();
				osw.close();
				os.close();
				
				LOGGER.info("SendSurveyDetails header fields..." + connection.getHeaderFields());	
				
				connection.connect();						
				
				LOGGER.info("SendSurveyDetails Response code : "+connection.getResponseCode());
				
				if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 210){
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuffer response = new StringBuffer();while((readLine=in.readLine())!=null)
					{
						response.append(readLine);
					}
						in.close();				
						jsonData=response.toString();
						LOGGER.info("SendSurveyDetails Response : "+response.toString());
					}
					else
					{
						LOGGER.error("SendSurveyDetails error in response");
						LOGGER.error("SendSurveyDetails reponse message"+ connection.getResponseMessage());			
					}	
					LOGGER.info("SendSurveyDetails invokeAPI -> "+endPoint+", json: "+new String(jsonData));
			}
			return jsonData;
		
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	public String SendSurveyXsignature(String requestBody) throws Exception
	{
		String jsonData = "";	
		
		try {
			//String endPoint 	= "https://test-endpoints-commonservices.clouddev.emiratesnbd.com/hash/4cf2df624c853811fcdd7bb1441bec83";		
			String endPoint 	=  Config.smsSignatureUrl;	
			LOGGER.info("SendSurveyXsignature invokeAPI -> started, url: " + endPoint);

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
		        
		        LOGGER.info("SendSurveyXsignature Request Body : " + requestBody);
		        
				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();				
				connection.setDoOutput(true);				
				connection.setInstanceFollowRedirects( false );
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");			
				connection.setUseCaches(false);	
			    connection.setRequestProperty("charset", "utf-8");
			    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));				
			   
				LOGGER.info("SendSurveyXsignature connection getRequestProperties : "+ connection.getRequestProperties());
				
				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    	
				osw.write(requestBody.toString());				
				osw.flush();
				osw.close();
				os.close();
				
				LOGGER.info("SendSurveyXsignature header fields..." + connection.getHeaderFields());	
				
				connection.connect();						
				
				LOGGER.info("SendSurveyXsignature Response code : "+connection.getResponseCode());
				
				if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 210){
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();while((readLine=in.readLine())!=null)
				{
					response.append(readLine);
				}
					in.close();				
					jsonData=response.toString();
					LOGGER.info("SendSurveyXsignature Response : "+response.toString());
				}
				else
				{
					LOGGER.error("SendSurveyXsignature error in response");
					LOGGER.error("SendSurveyXsignature reponse message"+ connection.getResponseMessage());			
				}	
				LOGGER.info("SendSurveyXsignature invokeAPI -> "+endPoint+", json: "+new String(jsonData));
			}
			return jsonData;
		
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	
	
  
}
