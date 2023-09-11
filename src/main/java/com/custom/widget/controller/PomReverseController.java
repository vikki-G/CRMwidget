package com.custom.widget.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.utils.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custom.widget.Config;
import com.custom.widget.model.CrmResponse;
import com.custom.widget.model.POMReverseResponse;
import com.custom.widget.model.pomLeads;
import com.custom.widget.services.TokenService;
import com.custom.widget.services.WidgetServices;
import com.custom.widget.util.StringUtil;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PomReverseController {

	private static final Logger LOGGER = LogManager.getLogger("PomReverse");

	@Autowired
	WidgetServices services;	
	
	@Value("${pom.reverse.scheduler.enable}")
	private String pomreverseschedulerEnabled;

	@Scheduled(cron = "${pom.reverseupdate.cron}")
	public void runPomReverseScheduler() {
		if(Boolean.parseBoolean(pomreverseschedulerEnabled)) {
			LOGGER.info("Current time is ::" + LocalDate.now());
			LOGGER.info("Pom Reverse Scheduler Started");
			pomReverseRequest();
			LOGGER.info("Pom Reverse Scheduler Ended");
		}
	}

	@GetMapping("/updateLeads")
	public Object pomReverseRequest() {
		POMReverseResponse response = null;
		InetAddress IP = null;
		String HostAddress="";
		try {
			IP = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("IP of my system is := "+IP.getHostAddress());
		
		if(StringUtil.isNotNullOrEmpty(IP.getHostAddress())) {
			HostAddress=IP.getHostAddress();
			
			LOGGER.info("HostAdress is "+ HostAddress);
		}
		
		try {
			ArrayList<pomLeads> leadlist = services.processPomReverseRequest("sp_pom_reverse_api_details",HostAddress);
			String reqbody = null;
			for (pomLeads pomld : leadlist) {
							
				reqbody = "{\r\n" + "    \"status\":\"" + pomld.getLeadStatus() + "\",\r\n" + "    \"subStatus\":\""
						+ pomld.getLeadSubStatus() + "\",\r\n" + "    \"remarks\": \"" + pomld.getRemarks() + "\"\r\n"
						+ "}";
				
				response = putPomReverse(reqbody, pomld);
				if (response.getResponse() == "updated") {
					services.updatetable(pomld.ReverseApiId, response);
					LOGGER.info("Leads status request was updated successful..");
				}else {
					services.updatetable(pomld.ReverseApiId, response);
					LOGGER.info("Leads status request was failed to update");
				}
			}
			if (response.toString() != null) {
				CrmResponse resp = new CrmResponse();
				resp.status = "Success";
				resp.message = "Updated";
				return resp;
			}
			CrmResponse resp = new CrmResponse();
			resp.status = "Failed";
			resp.message = "It is not updated";
			return resp;
		} catch (Exception e) {
			LOGGER.error(e.toString());
			CrmResponse resp = new CrmResponse();
			resp.status = "Failed";
			resp.message = e.toString();
			return resp;
		}
	}
	
	

	public static POMReverseResponse putPomReverse(String requestBody, pomLeads pomld) throws Exception {
		POMReverseResponse pomReverseResponse = new POMReverseResponse();
		
		int time=Config.POMTOKENACCESSTIME;
		Date aftertokencall = null;
		Long lasttokentime = null;
		
		Date today = StringUtil.getTodayDateWithTime();
		
		if(Config.LASTTOKENACCESSTTIMEDELAY!=null) {
			lasttokentime=Config.LASTTOKENACCESSTTIMEDELAY.getTime();
		}
		
		LOGGER.info("Pom reverse logic contitions "+ " " + Config.resultset + " " + 
		Config.LASTTOKENACCESSTTIMEDELAY + " "+ today + " "+ lasttokentime+ " " + today.getTime() +" "+ Config.nooftimestokenacces);
		
		
		if(!Config.resultset|| Config.LASTTOKENACCESSTTIMEDELAY.getTime() <= today.getTime()){
			 Config.tokenGenerateKey =TokenService.authorizationTokenTest("sms",aftertokencall);
		}
		
		String authorizationToken = "Bearer " + Config.tokenGenerateKey;
		// LOGGER.info("putPomReverse authorizationToken: " + authorizationToken);
		try {

			// String endPoint =
			// "https://bawabauat.clouduat.emiratesnbd.com/engagement/sales/v1/leads/"+pomld.LeadReferenceId;
			String endPoint = Config.pomUrl + pomld.LeadReferenceId;
			LOGGER.info("putPomReverse invokeAPI -> started, url: " + endPoint);

			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

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

			if (endPoint != null) {
				String readLine = null;
				byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
				int postDataLength = postData.length;

				LOGGER.info("putPomReverse Request Body : " + requestBody);
				String UniqueReferenceCode="OMNPRTAC"+pomld.getReverseApiId();
				LOGGER.info("Pom Unique Reference ID For the Request is : " + UniqueReferenceCode);
				

				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
				connection.setDoOutput(true);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("Authorization", authorizationToken);
				connection.setRequestProperty("Channel-Id", "OMN");
				connection.setRequestProperty("Financial-Id", "EBI");
				connection.setRequestProperty("Unique-Reference-Code",UniqueReferenceCode);
				connection.setRequestProperty("Client-Timestamp", "1527058644945");
				connection.setUseCaches(false);
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

				LOGGER.info("putPomReverse connection getRequestProperties : " + connection.getRequestProperties());

				OutputStream os = connection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
				osw.write(requestBody.toString());
				osw.flush();
				osw.close();
				os.close();

				LOGGER.info("putPomReverse header fields..." + connection.getHeaderFields());

				connection.connect();

				LOGGER.info("putPomReverse Response code : " + connection.getResponseCode());
				
				pomReverseResponse.setStatusCode(String.valueOf(connection.getResponseCode()));

				if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 210) {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuffer response = new StringBuffer();
					while ((readLine = in.readLine()) != null) {
						response.append(readLine);
					}
					in.close();
					//jsonData = "updated";
					pomReverseResponse.setResponse("updated");
					LOGGER.info("putPomReverse Response : " + response.toString());
				}else {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
					StringBuffer response = new StringBuffer();
					while ((readLine = in.readLine()) != null) {
						response.append(readLine);
					}
					in.close();
					//jsonData = response.toString();
					pomReverseResponse.setResponse(response.toString());
					LOGGER.error("putPomReverse Error response: " + response.toString());

				}
				LOGGER.info("putPomReverse invokeAPI -> " + endPoint + ", Status Code: " + pomReverseResponse.getStatusCode()+", Response" +pomReverseResponse.getResponse());
			}
			return pomReverseResponse;

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			pomReverseResponse.setResponse(e.getMessage());
			return pomReverseResponse;
		}
	}

}
