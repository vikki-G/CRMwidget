package com.custom.widget.repository;

import java.sql.Connection;
import java.sql.DriverManager;

import java.util.ResourceBundle;


import org.springframework.stereotype.Repository;
@Repository
public class DbConnect {

    private String driverNameKey = "spring.datasource.driver-class-name";
    private String urlKey = "spring.datasource.url";
    private String userNameKey = "spring.datasource.username";
    private String passWordKey = "spring.datasource.password";

    
    public Connection getConnection() throws Exception {
        Connection connection = null;
        try {
            ResourceBundle rb = ResourceBundle.getBundle("application");
            String driverName = rb.getString(driverNameKey);
            String url = rb.getString(urlKey);
            String userName = rb.getString(userNameKey);
            String encryptedPassWord = rb.getString(passWordKey);

            Class.forName(driverName);
            connection = DriverManager.getConnection(url, userName, encryptedPassWord);

            System.out.println("Database Connection Success!!");
           
        } catch (Exception e) {
            System.out.println("Database Connection Exception: " + e);
            
        }

        return connection;
    }
}


    

