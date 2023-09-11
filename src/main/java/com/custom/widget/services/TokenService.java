package com.custom.widget.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
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

import com.custom.widget.Config;
import com.custom.widget.util.StringUtil;

public class TokenService {

	// private static final Logger LOGGER = LogManager.getLogger("Widget");
	private static final Logger LOGGER2 = LogManager.getLogger("PomReverse");

	public static String authorizationToken(String tokentype) {
		String accessToken = null;
		try {
			// String url =
			// "https://rhsso-uat.clouduat.emiratesnbd.com/auth/realms/enbd/protocol/openid-connect/token";
			String url = Config.tokenUrl;
			String endPoint = url;
			byte[] jsonData = "".getBytes();
			LOGGER2.info("authorizationToken -> started, url: " + endPoint);

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
			String urlParameters = Config.tokenDetails;

			if (tokentype == "sms") {
				// urlParameters =
				// "grant_type=client_credentials&client_id=8bfee134&client_secret=514d57300a032b97d27190333ffb7870";
				urlParameters = Config.smsTokenDetails;
			}

			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;

			String readLine = null;
			HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			LOGGER2.info("Connection Request Properties  : " + connection.getRequestProperties());
			OutputStream os = connection.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			osw.write(urlParameters.toString());
			osw.flush();
			osw.close();
			os.close();
			connection.connect();

			// logger.info(requestId + " : " +"Response code:
			// "+connection.getResponseCode());

			if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 210) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();
				while ((readLine = in.readLine()) != null) {
					response.append(readLine);
				}
				in.close();

				jsonData = response.toString().getBytes();

				// LOGGER.info("Success response jsonData : "+new String(jsonData));
				String tokenResponse = new String(jsonData);
				JSONObject JSONObject = new JSONObject(tokenResponse);
				accessToken = JSONObject.getString("access_token");

			} else {
				LOGGER2.error("Failure response jsonData : " + new String(jsonData));
			}

			// LOGGER.info("invokeAPI -> "+endPoint+", json: "+new String(jsonData));
			// LOGGER.info("accessToken in Service : "+accessToken);
			return accessToken;

		} catch (Exception e) {
			LOGGER2.error(e.getMessage(), e);
			return accessToken;
		}
	}

	public static String authorizationTokenTest(String tokentype, Date aftertokentime) {
		String accessToken = null;
		try {
			// String url =
			// "https://rhsso-uat.clouduat.emiratesnbd.com/auth/realms/enbd/protocol/openid-connect/token";
			String url = Config.tokenUrl;
			String endPoint = url;
			byte[] jsonData = "".getBytes();
			LOGGER2.info("authorizationToken -> started, url: " + endPoint);

			LOGGER2.info("Time based Token generation Logic changes started");
			Config.LASTTOKENACCESSTTIMESTART = LocalDateTime.now();
			aftertokentime = StringUtil.getTimeAfterXSeconds(Config.POMTOKENACCESSTIME);
			Config.LASTTOKENACCESSTTIMEDELAY = aftertokentime;
			Config.resultset = true;
			LOGGER2.info("Token time aftertime : " + aftertokentime);
			LOGGER2.info("Time based Token generation Logic changes Ented");
			Config.nooftimestokenacces = Config.nooftimestokenacces + 1;

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
			String urlParameters = Config.tokenDetails;

			if (tokentype == "sms") {
				// urlParameters =
				// "grant_type=client_credentials&client_id=8bfee134&client_secret=514d57300a032b97d27190333ffb7870";
				urlParameters = Config.smsTokenDetails;
			}

			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;

			String readLine = null;
			HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			LOGGER2.info("Connection Request Properties  : " + connection.getRequestProperties());
			OutputStream os = connection.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			osw.write(urlParameters.toString());
			osw.flush();
			osw.close();
			os.close();
			connection.connect();

			// logger.info(requestId + " : " +"Response code:
			// "+connection.getResponseCode());

			if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 210) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();
				while ((readLine = in.readLine()) != null) {
					response.append(readLine);
				}
				in.close();

				jsonData = response.toString().getBytes();

				// LOGGER.info("Success response jsonData : "+new String(jsonData));
				String tokenResponse = new String(jsonData);
				JSONObject JSONObject = new JSONObject(tokenResponse);
				Config.tokenGenerateKey = JSONObject.getString("access_token");

			} else {
				LOGGER2.error("Failure response jsonData : " + new String(jsonData));
			}

			// LOGGER.info("invokeAPI -> "+endPoint+", json: "+new String(jsonData));
			// LOGGER.info("accessToken in Service : "+accessToken);

			return Config.tokenGenerateKey;

		} catch (Exception e) {
			LOGGER2.error(e.getMessage(), e);
			return Config.tokenGenerateKey;
		}
	}

}
