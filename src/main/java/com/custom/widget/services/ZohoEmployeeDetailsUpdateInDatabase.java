package com.custom.widget.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;

import com.custom.widget.model.PeopleBO;
import com.custom.widget.repository.DbConnect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ZohoEmployeeDetailsUpdateInDatabase {

	 private static final Logger logger = LogManager.getLogger(ZohoEmployeeDetailsUpdateInDatabase.class);
    // Create an instance of the DbConnect class to manage database connections
    DbConnect conn = new DbConnect();

    // Method to fetch and process data from a JSON response
    public void zohoEmployedetails(Object response) {
        try {
            // Create an ObjectMapper to work with JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = response.toString(); // Convert StringBuffer to String if necessary
            // Parse the JSON response
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            
            if (rootNode != null) {
                // Extract the "result" node from the JSON response
                JsonNode resultNode = rootNode.path("response").path("result");
                logger.info("Getting Employee Details");
                PeopleBO peopleBOArray;
                List<PeopleBO> peopleBOs = new ArrayList<>();

                for (JsonNode employeeNode : resultNode) {
                    String dynamicKey = employeeNode.fieldNames().next();
                    JsonNode employeeDetailsArray = employeeNode.get(dynamicKey);
                    String jsonString = employeeDetailsArray.toString();
                    String outputString = jsonString.replace("[", "").replace("]", "");

                    // Deserialize JSON data into a PeopleBO object
                    peopleBOArray = objectMapper.readValue(outputString, PeopleBO.class);
                    peopleBOs.add(peopleBOArray);
                }

                // Establish a database connection and delete existing data
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                connection = conn.getConnection();
                String deleteQuery = "USE INCITRACK_DB DELETE FROM EMPLOYEE";
                preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.execute();
                logger.info("Previous Data Deleted Successfully");
                // Insert new data into the database
                UpdateEmployeDetails(peopleBOs);
            } else {
            	logger.info("Employe data is Null!");
            }
        } catch (Exception e) {
           logger.error("Error"+e); 
        }
    }

    // Method to insert data into the database
    public void UpdateEmployeDetails(List<PeopleBO> persistBOs) throws Exception {
    	logger.info("Insert Data into the Database");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        connection = conn.getConnection();
        try {
            // Generate the SQL query for inserting data
            String queryValuesPart = generateValuesPartForQuery(persistBOs);
            String insertQuery = "USE INCITRACK_DB INSERT INTO EMPLOYEE (VC_EMPLOYEE_EMAILID,DT_EMPLOYEE_DOB,VC_EMPLOYEE_PHOTO,VC_EMPLOYEE_LOCATIONNAME,VC_EMPL_DEPARTMENT,VC_EMPLOYEE_REPORTING_TO_MAILID,REFID,VC_EMPLOYEE_REPORTING_TO,VC_EMPLY_DESIGNATION, VC_EMPLOEE_FIRSTNAME,VC_EMPLOY_MOBILENUMBER, VC_EMPLOYEE_LASTNAME, VC_EMPLOYEE_EMPLOYEEID) VALUES "
                    + queryValuesPart;
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.execute();
            logger.info("Data Insert Successfully");
            logger.info("Current time is ::" + LocalDate.now());
        } catch (Exception e) {
           logger.error("Error while inserting data into database"+e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // Method to generate the values part of the SQL query for insertion
    public static String generateValuesPartForQuery(List<PeopleBO> peopleBOs) {
        String queryValues = "";
        int size = peopleBOs.size();
        logger.info("Generate the values part of the SQL query for insertion");
        for (int index = 0; index < size; index++) {
            PeopleBO persistBO = peopleBOs.get(index);
            // Generate the values for insertion
            String queryValuesPart = "('" + persistBO.getEmailId() + "','" + persistBO.getDateOfBirth() + "','"
                    + persistBO.getPhoto() + "','" + persistBO.getLocationName() + "','" + persistBO.getDepartment()
                    + "','" + persistBO.getReportingToMailId() + "'," + persistBO.getZohoId() + ",'"
                    + persistBO.getReportingTo() + "','" + persistBO.getDesignation() + "','" + persistBO.getFirstName()
                    + "','" + persistBO.getMobile() + "','" + persistBO.getLastName() + "','"
                    + persistBO.getEmployeeId() + "')";

            if (index < size - 1) {
                queryValuesPart = queryValuesPart + ",";
            }

            queryValues = queryValues + queryValuesPart;
        }
        return queryValues;
    }
}
