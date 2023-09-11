package com.custom.widget.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custom.widget.Config;
import com.custom.widget.model.CrmResponse;
import com.custom.widget.services.WidgetServices;
import com.custom.widget.util.StringUtil;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AcmController {

	private static final Logger LOGGER = LogManager.getLogger("ACMSchduler");

	@Autowired
	WidgetServices services;
	
	@Scheduled(cron = "${acm.runAcmAgentScheduler}")
	public void runAcmAgentScheduler() throws Exception {
		LOGGER.info("Current time is ::" + LocalDate.now());
		LOGGER.info("ACM Agent Scheduler Started");
		int numbers = services.checkAcmAgentTemptable();
		if(!(numbers >=1)) {
			insertACMAttributes();
			LOGGER.info("Started moving data from temp table...");
			services.callAcmAgent();
			LOGGER.info("Moving data from Temp table to Acm table...");
			LOGGER.info("ACM Agent Scheduler Ended");
		}else {
			LOGGER.info("Temp table to Acm table already inserted current date");
		}
		
	}

	

	@GetMapping("/insertAcm")
	public Object insertACMAttributes() {
		String attributesResponse = null;
		String agentsResponse = null;
		String attributesFormat = "";
		String emailChannel = "No";
		String agentName = null;
		String agentId = null;
		String entity = null;
		boolean deleteCheck = false;
		try {
		
			agentsResponse = getAcmAgents();
			if (agentsResponse.toString() != null) {
				JSONArray agentList = new JSONArray(agentsResponse.toString());
				for (int agentsindex = 0; agentsindex < agentList.length(); agentsindex++) {
					attributesFormat = "";
					emailChannel = "No";
					agentName = null;
					agentId = null;
					entity = null;
					JSONObject agentObject = agentList.getJSONObject(agentsindex);
					String username = agentObject.getString("UserName");

					attributesResponse = getAcmAttributes(username);
					if (attributesResponse.toString() != null && attributesResponse.toString() != "error") {
						JSONObject attributesObject = new JSONObject(attributesResponse.toString());
						JSONArray attributesList = attributesObject.getJSONArray("Attributes");
						agentName = attributesObject.getString("AD_Username");
						agentId = attributesObject.getString("AgentId");
						if (attributesList != null) {
							for (int attrindex = 0; attrindex < attributesList.length(); attrindex++) {
								JSONObject attrobject = attributesList.getJSONObject(attrindex);
								JSONObject categoryList = attrobject.getJSONObject("Category");
								attributesFormat += categoryList.getString("Name") + ":" + attrobject.getString("Value")
										+ "|";
							}
						}
						JSONArray channelsList = attributesObject.getJSONArray("Accounts");
						if (channelsList != null) {
							for (int chlindex = 0; chlindex < channelsList.length(); chlindex++) {
								JSONObject chlobject = channelsList.getJSONObject(chlindex);
								JSONArray typeList = chlobject.getJSONArray("Channels");
								if (typeList.toString() != null && typeList.toString().contains("Email")) {
									emailChannel = "yes";
								}
							}
						}
					} else {
						LOGGER.info("User Name is failed to get details ::" + username + ". Response ::"
								+ attributesResponse.toString());
					}
					LOGGER.info("Attributes For Agent ::" + agentName + ".." + attributesFormat);
					LOGGER.info("emailChannel..." + emailChannel);
					if (deleteCheck == false) {
						services.deleteAcmAgent();
						deleteCheck = true;
					}
					if (attributesFormat != null && attributesFormat.contains("ENBD")) {
						entity = "ENBD";
					}
					if (attributesFormat != null && attributesFormat.contains("EI")) {
						entity = "EI";
					}
					if (attributesFormat != null && attributesFormat.contains("ENBD")
							&& attributesFormat.contains("EI")) {
						entity = "ENBD,EI";
					}
					 services.insertAcmAgent(username,agentName,attributesFormat,emailChannel,agentId,entity);
				}
			}
			if (attributesResponse.toString() != null) {
				CrmResponse resp = new CrmResponse();
				resp.status = "Success";
				resp.message = "Data Updated Successfully";
				return resp;
			}
			CrmResponse resp = new CrmResponse();
			resp.status = "Failed";
			resp.message = "Data issue";
			return resp;
		} catch (Exception e) {
			LOGGER.error(e.toString());
			CrmResponse resp = new CrmResponse();
			resp.status = "Failed";
			resp.message = e.toString();
			return resp;
		}
	}

	public String getAcmAgents() throws Exception {
		String jsonData = "";
		try {
			// String endPoint =
			// "https://10.100.7.68/ACMWEBAPI/Locations('MCENBD')/OceanaUsers";
			String endPoint = Config.acmUrl;
			LOGGER.info("getAcmAgents invokeAPI -> started, url: " + endPoint);

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

				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("Authorization", getAuthorizationToken());

				LOGGER.info("getAcmAgents connection getRequestProperties : " + connection.getRequestProperties());
				LOGGER.info("getAcmAgents header fields..." + connection.getHeaderFields());

				connection.connect();

				LOGGER.info("getAcmAgents Response code : " + connection.getResponseCode());

				if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 210) {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuffer response = new StringBuffer();
					while ((readLine = in.readLine()) != null) {
						response.append(readLine);
					}
					in.close();
					jsonData = response.toString();
					LOGGER.info("getAcmAgents Response : " + response.toString());
				} else {
					LOGGER.error("getAcmAgents error in response");
					LOGGER.error("getAcmAgents reponse message" + connection.getResponseMessage());
				}
				LOGGER.info("getAcmAgents invokeAPI -> " + endPoint + ", json: " + new String(jsonData));
			}
			return jsonData;

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	public String getAcmAttributes(String username) {
		String jsonData = "";
		try {
			// String endPoint =
			// "https://10.100.7.68/ACMWEBAPI/Locations('MCENBD')/OceanaUsers('"+username+"')";
			String endPoint = Config.acmUrl + "('" + username + "')";
			LOGGER.info("getAcmAttributes invokeAPI -> started, url: " + endPoint);

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

				HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("Authorization", getAuthorizationToken());

				LOGGER.info("getAcmAttributes connection getRequestProperties : " + connection.getRequestProperties());
				LOGGER.info("getAcmAttributes header fields..." + connection.getHeaderFields());

				connection.connect();

				LOGGER.info("getAcmAttributes Response code : " + connection.getResponseCode());

				if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 210) {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuffer response = new StringBuffer();
					while ((readLine = in.readLine()) != null) {
						response.append(readLine);
					}
					in.close();
					jsonData = response.toString();
					LOGGER.info("getAcmAttributes Response : " + response.toString());
				} else {
					LOGGER.error("getAcmAttributes error in response");
					LOGGER.error("getAcmAttributes reponse message" + connection.getResponseMessage());
					jsonData = "error";
				}
				LOGGER.info("getAcmAttributes invokeAPI -> " + endPoint + ", json: " + new String(jsonData));
			}
			return jsonData;

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	private String getAuthorizationToken() {
		// String encodeBytes =
		// Base64.getEncoder().encodeToString(("itnv"+":"+"N0p@inN0g@in2023").getBytes());
		String encodeBytes = Base64.getEncoder()
				.encodeToString((Config.acmUsername + ":" + Config.acmPassword).getBytes());
		System.out.println("Basic Authorization..." + "Basic " + encodeBytes);
		return "Basic " + encodeBytes;
	}
}
