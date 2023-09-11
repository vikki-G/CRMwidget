package com.custom.widget;

import java.time.LocalDateTime;
import java.util.Date;

public class Config
{
	public static String crmUrl;
	public static String tokenUrl;
	public static String tokenDetails;
	
	public static String smsUrl;	
	public static String smsSignatureUrl;
	public static String smsTokenDetails;
	
	public static String pomUrl;	
	public static String tcpIp;
	public static String tcpPort;	
	
	public static String surveyUrl;
	public static String acmUrl;	
	public static String acmUsername;	
	public static String acmPassword;	
	
	public static String pomDisUrl;	
	public static String pomDisUsername;	
	public static String pomDisPassword;	
	
    public static String ENCRYPTIONKEY;
    public static Integer POMTOKENACCESSTIME;
    
	public static volatile LocalDateTime LASTTOKENACCESSTTIMESTART = LocalDateTime.now();
	public static volatile Date LASTTOKENACCESSTTIMEDELAY;
	public static Boolean resultset=false;
	public static String tokenGenerateKey;
	public static Integer nooftimestokenacces=0;
	
}
