package com.custom.widget.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.custom.widget.Config;
import com.custom.widget.model.CrmResponse;

public class SocketService {
	
	private static final Logger LOGGER = LogManager.getLogger("TCPCallTag");

	public String SendTcpMessage (String agentExtension, String cardNumber, String cifNumber) 
			throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {    	        
	        InetAddress host = InetAddress.getLocalHost();
	        Socket socket = null;
	        ObjectOutputStream oos = null;
	        ObjectInputStream ois = null;    
	        String tcpipaddress = Config.tcpIp;   //10.119.8.78
	        String tcpport = Config.tcpPort;  //4444
	        //int port = Integer.parseInt(Config.tcpPort);  //4444
	        String tcpmessage =  null; 
	        String result = null; 
	        //String bindingmsg = null;
	        
	        //bindingmsg = agentExtension+","+cardNumber+","+cifNumber+",";	       
	        //tcpmessage = bindingmsg+"attributes:product:'OTHERS'|customerSegment':['E20']|'service':['TechnicalSupport']|'VoiceEWT':['PrimaryVoice']|";
	        tcpmessage = agentExtension+","+cardNumber+","+cifNumber;
	        
	        String[] ipaddresslist= tcpipaddress.split(",");
	        
	        String[] portlist= tcpport.split(",");
	        
	        for (int i = 0; i < ipaddresslist.length; i++) {
	        	
	        	  int port = Integer.parseInt(portlist[i]);  
	        	try
				{		

	        	    LOGGER.info("Message.."+tcpmessage);
	      	        LOGGER.info("Ipaddress.."+ipaddresslist[i] +" Port..."+port);
	      	        LOGGER.info("Establish socket connection to server");
	      	        
	      	        socket = new Socket(ipaddresslist[i], port);
	      	        
	      	        //write to socket 	       
	      	        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));	
	      	        LOGGER.info("Sending request to Socket Server");
	      	       // tcpmessage = bindingmsg+"|attributes:product:'OTHERS'|customerSegment':['E20']|'service':['TechnicalSupport']|'VoiceEWT':['PrimaryVoice']|";      	  
	      	        LOGGER.info("Sending Message.."+tcpmessage);
	      	        out.write(tcpmessage);	      
	      	        LOGGER.info("Message sent..");	   
	      	        out.flush();
	      	        out.close();	  
	      	        socket.close();
	      	        return "done";   	    
				} 
				catch (Exception e) 
				{
					   LOGGER.info("Failed to connect the socket server");
					   LOGGER.error(e.getMessage(), e);
					   LOGGER.info("Failed Ipaddress is: "+ipaddresslist[i] +" Port : "+port);
					   result= "Failed";   	 
				}
	        	}
	        
	        return result;
	      
	          
    }
}