package com.custom.widget.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
import com.custom.widget.model.Response;
import com.custom.widget.services.TokenService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CrmController {

	private static final Logger LOGGER = LogManager.getLogger("CRMPopup");	
	
	@GetMapping("/getTest")
	public String getTest()
	{
		LOGGER.debug("test log working");
		return "Working Test";
	}

    @PostMapping("/getCrmUrl")
	public Object customWidgetPostRequest(@RequestBody String agentinfo)
	{
		String response = null;		
			try
			{						
					response = getENBDCrmUrl(agentinfo);	
					if(response.toString() != null) {
						JSONObject resJson = new JSONObject(response.toString());  						
						String resUrl = resJson.getString("URL");  
						Response resp = new Response();
						resp.status = "Success";
						resp.URL= resUrl;
						return resp;		
					}
					if(response == "tokenerror") {	
						Response resp = new Response();
						resp.status = "Error";
						resp.URL = "";
						return resp;
					}
					if(response == null || response.toString() == "")
					{
						Response resp = new Response();
						resp.status = "Success";
						resp.URL= "";
						return resp;
					}						
					Response resp = new Response();
					resp.status = "Error";
					resp.URL= "";
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
    
    @PostMapping("/postCrmUrl")
	public Object customWidgetPostCrmUrlRequest(@RequestBody String requestInfo)
	{
		String response = null;		
		String postResponse = null;		
			try
			{				
				JSONObject requestInfoJson = new JSONObject(requestInfo);  						
				String agentName = requestInfoJson.getString("agentId");  
				String agentDetails = "{\r\n"
						+ "    \"AgentID\":\""+agentName+"\"\r\n"
						+ "}";
				LOGGER.info("Agent Handle :"+agentDetails);   
				response = getENBDCrmUrl(agentDetails);					
				if(response.toString() != null) {
					JSONObject resJson = new JSONObject(response.toString());  						
					String resUrl = resJson.getString("URL");  
					if(resUrl.toString() != null && resUrl.toString().length()>0 &&resUrl.toString()!="") {
						    postResponse = postENBDCrmUrl(resUrl,requestInfo);
							if(postResponse.toString() != null) {							
									CrmResponse resp = new CrmResponse();
									resp.status = "Success";
									if(postResponse.toString() == "") {	
										resp.message = "Successful";
									}else {
										resp.message = postResponse.toString();
									}
									return resp;
							}
					}else {
						CrmResponse resp = new CrmResponse();
						resp.status = "Failed";
						resp.message= "Url is empty";
						return resp;	
					}
				}			
				CrmResponse resp = new CrmResponse();
				resp.status = "Success";
				resp.message= "Url is empty";
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
      
	public class MyHttpGetWithEntity extends HttpEntityEnclosingRequestBase {
	    public final static String GET_METHOD = "GET";
	    
	    public MyHttpGetWithEntity(final URI uri) {
	        super();
	        setURI(uri);
	    }
	    
	    public MyHttpGetWithEntity(final String uri) {
	        super();
	        setURI(URI.create(uri));
	    }

	    @Override
	    public String getMethod() {
	        return GET_METHOD;
	    }
	}
	
	public String getENBDCrmUrl(String agentname) throws Exception
	{
		String accessToken = TokenService.authorizationToken("crm");
		String authorizationToken =  "Bearer "+accessToken;
		String result = null;		
		String UniqueReferenceCode = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
		
		TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };          
		
	  CloseableHttpClient httpClient = HttpClients.createDefault();
	 try {		       	
			  	SSLContext sc = SSLContext.getInstance("SSL");
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( sc);
	            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());		
	            
	            String payload = agentname ;
	            StringEntity requestEntity = new StringEntity(agentname,ContentType.APPLICATION_JSON);			 
	         
	            //MyHttpGetWithEntity getrequest = new MyHttpGetWithEntity("https://bawabauat.clouduat.emiratesnbd.com/inbound/v1/ivr/rest/api/v1/middleware");
	            MyHttpGetWithEntity getrequest = new MyHttpGetWithEntity(Config.crmUrl);
	            getrequest.setEntity(new StringEntity(agentname,ContentType.APPLICATION_JSON));           
	            
	            // headers           
	            getrequest.addHeader("Accept", "application/json");
	            getrequest.addHeader("Accept-Language", "en");
	            getrequest.addHeader("Authorization", authorizationToken);
	            getrequest.addHeader("Avaya-Authorization", "Basic YXZheWE6YXZheWE=");
	            getrequest.addHeader("Channel-Id", "OMN");
	            getrequest.addHeader("Client-Ip", "1.1.1.1");
	            getrequest.addHeader("Client-Timestamp", "1527058644945");
	            getrequest.addHeader("Financial-Id", "EIB");
	            getrequest.addHeader("Unique-Reference-Code", UniqueReferenceCode);
	            getrequest.addHeader("accept", "application/json");
	            getrequest.addHeader("cache-control", "no-cache");
	            getrequest.addHeader("Content-Type", "application/json");   
	            getrequest.addHeader("crm-fqdn", "");   
	            
	            LOGGER.info("request parameters..."+getrequest.getAllHeaders());   
	            CloseableHttpResponse response = httpClient.execute(getrequest);	            
	            //LOGGER.info("response parameters....."+response.getAllHeaders());    
	            try {
	                // Get HttpResponse Status	                       
	            	LOGGER.info("Status Code..."+response.getStatusLine().getStatusCode());   
	            	LOGGER.info("Status..."+response.getStatusLine().getReasonPhrase()); 
	            	LOGGER.info("Status Reason..."+response.getStatusLine().toString());     

	                HttpEntity entity = response.getEntity();	               
	                if (entity != null) {	                 
	                    result = EntityUtils.toString(entity);
	                    LOGGER.info("Response...." + result);
	                }
	                
	            } finally {
	                response.close();
	            }
	        } finally {
	            httpClient.close();
	        }    
		
		return result;		
	}
	
	public String postENBDCrmUrl(String apiUrl, String requestInfo) throws Exception
	{
		String jsonData = "";
		try {
			
			String accessTokenPostENBD = TokenService.authorizationToken("crm");
			String authorizationPostENBDToken =  "Bearer "+accessTokenPostENBD;
			
			JSONObject requestInfoJson = new JSONObject(requestInfo);  						
			String UniqueReferenceCode = requestInfoJson.getString("contextstoreId");  
			String Entity = requestInfoJson.getString("entityId");  
			
			String FinancialId = Entity.toUpperCase()=="ENBD"?"EBI":"EIB";  
			
			String endPoint 	= Config.crmUrl;
			
			LOGGER.info("postENBDCrmUrl invokeAPI -> started, url: " + endPoint);

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

			if(apiUrl != null)
			{
				String readLine = null;	
				LOGGER.info("postENBDCrmUrl Received URL : " + apiUrl);
				URL url = new URL(apiUrl);
		        byte[] postData = requestInfo.getBytes(StandardCharsets.UTF_8);
		        int postDataLength = postData.length;
		        
		        LOGGER.info("postENBDCrmUrl Request Body : " + requestInfo);
		        
		        LOGGER.info("postENBDCrmUrl URL Format : " + url);
		        
		        LOGGER.info("postENBDCrmUrl crm-fdqn Request : " + url.getHost());
		        
				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();				
				connection.setDoOutput(true);				
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");				
				connection.setRequestProperty("Avaya-Authorization", "Basic YXZheWE6YXZheWE=");
				connection.setRequestProperty("Accept-Language", "en");
				connection.setRequestProperty("Authorization", authorizationPostENBDToken);	   
				connection.setRequestProperty("Channel-Id", "OMN");
				connection.setRequestProperty("Client-Ip", "1.1.1.1");
				connection.setRequestProperty("Client-Timestamp", "1527058644945");
				connection.setRequestProperty("Financial-Id", FinancialId);
				connection.setRequestProperty("Unique-Reference-Code", UniqueReferenceCode);	      
				connection.setRequestProperty("crm-fqdn",url.getHost());  
				connection.setUseCaches(false);	
			    connection.setRequestProperty("charset", "utf-8");
			    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));				
			   
				LOGGER.info("postENBDCrmUrl connection getRequestProperties : "+ connection.getRequestProperties());
				
				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    	
				osw.write(requestInfo.toString());				
				osw.flush();
				osw.close();
				os.close();
				
				LOGGER.info("postENBDCrmUrl header fields..." + connection.getHeaderFields());	
				
				connection.connect();						
				
				LOGGER.info("postENBDCrmUrl Response code : "+connection.getResponseCode());
				
				if(connection.getResponseCode() >= 200 && connection.getResponseCode() < 210){
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						StringBuffer response = new StringBuffer();while((readLine=in.readLine())!=null)
						{
							response.append(readLine);
						}in.close();
						// print result
						jsonData=response.toString();
						LOGGER.info("postENBDCrmUrl Response : "+response.toString());
				}
				else
				{
					LOGGER.error("postENBDCrmUrl error in response");
					LOGGER.error("postENBDCrmUrl reponse message"+ connection.getResponseMessage());			
				}			
			
				LOGGER.info("postENBDCrmUrl invokeAPI -> "+endPoint+", json: "+new String(jsonData));
			}
			return jsonData;
		
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}	
	
}
