package com.custom.widget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.custom.widget.util.StringUtil;

@SpringBootApplication
@EnableScheduling
public class CustomWidgetApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomWidgetApplication.class, args);
	}

	

}
