package com.custom.widget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {
	 
	private static final Logger logger = LogManager.getLogger("Widget");
	
	 @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		
		logger.info("Application Started");
		return application.sources(CustomWidgetApplication.class);
	
	 }

}
