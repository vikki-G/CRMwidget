package com.custom.widget.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.custom.widget.services.WidgetServices;
import com.custom.widget.services.ZohoEmployeeDetailsUpdateInDatabase;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SaveEmployeeDetailsController {

	private static final Logger LOGGER = LogManager.getLogger("ZohoAPIScheduler");

	public static void main(String[] args) {
		SaveEmployeeDetailsController jj=new SaveEmployeeDetailsController();
		jj.zohoAPIcallgetEmployeeDetails();
	}
	@Autowired
	WidgetServices services;
    
	
	@Scheduled(cron = "${cronExpression}")
	public void zohoAPIcallgetEmployeeDetails() {
		services = new WidgetServices();
		ZohoEmployeeDetailsUpdateInDatabase Employeedetails = new ZohoEmployeeDetailsUpdateInDatabase();
		LOGGER.info("Current time is ::" + LocalDate.now());
		LOGGER.info("ZohoAPIcall Tokengenerate Started");
		String tokenkey = services.callAPITokenGenerate();
		LOGGER.info("ZohoExternalAPI to Get Details started");
		String fetchZohoEmployeeData= services.callAPIgetRecords(tokenkey);
		LOGGER.info("Delete Previous Data and insert new data Started");
		Employeedetails.zohoEmployedetails(fetchZohoEmployeeData);
		LOGGER.info("ZohoAPIcall Tokengenerate Ended");
	}
}
