package com.custom.widget.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.custom.widget.model.CrmResponse;
import com.custom.widget.services.SocketService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TcpController {

	private static final Logger LOGGER = LogManager.getLogger("TCPCallTag");		
	
    @PostMapping("/postTcpMsg")
	public Object customWidgetTcpRequest(@RequestBody String tcpBody)
	{
		String response = null;					
			try
			{				
				JSONObject tcpBodyJson = new JSONObject(tcpBody);  						
				String agentExtension = tcpBodyJson.getString("agentextension"); 
				String cardNumber = tcpBodyJson.getString("cardnumber"); 
				String cifNumber = tcpBodyJson.getString("cifnumber"); 				
				
				SocketService sockClnt  =  new SocketService();
				response = sockClnt.SendTcpMessage(agentExtension,cardNumber,cifNumber);
				if(response == "done")
				{				
					CrmResponse resp = new CrmResponse();
					resp.status = "Success";
					resp.message= "Message Sent";	
					return resp;
				}	
				CrmResponse resp = new CrmResponse();
				resp.status = "Failed";
				resp.message= "Message Not Sent";	
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
}
