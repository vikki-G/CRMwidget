package com.custom.widget;

import org.apache.logging.log4j.LogManager;
import java.sql.SQLException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import org.springframework.util.ResourceUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationRunner;

@Component
public class HikariLoader implements ApplicationRunner
{
    private static final Logger logger;
    private final HikariDataSource hikariDataSource;
    
    @Autowired
    public HikariLoader(final HikariDataSource hikariDataSource) {
        try {
            final File a_config = ResourceUtils.getFile("classpath:application.properties");
            final Properties a_prop = new Properties();
            a_prop.load(new FileInputStream(a_config.toPath().toString()));
            if (a_prop.get("maximumPoolSize") != null && a_prop.get("minimum-idle") != null) {
                final int max = Integer.parseInt((String)a_prop.get("maximumPoolSize"));
                final int miniIdle = Integer.parseInt((String)a_prop.get("minimum-idle"));
                //Config.ENCRYPTIONKEY = a_prop.getProperty("password.encryption.secret-key");
                final String sPassword = Decryption.getDecryptedData(a_prop.getProperty("db.password"));
                hikariDataSource.setUsername(a_prop.getProperty("db.username"));
                hikariDataSource.setPassword(sPassword);
                hikariDataSource.setMaximumPoolSize(max);
                hikariDataSource.setMinimumIdle(miniIdle);
            }
            Config.tokenUrl =  a_prop.getProperty("token.api.url");
            Config.tokenDetails =  a_prop.getProperty("token.api.details");            
            Config.crmUrl =  a_prop.getProperty("crm.api.url");
            Config.smsUrl =  a_prop.getProperty("sms.api.url");
            Config.smsSignatureUrl =  a_prop.getProperty("sms.api.signature.url");
            Config.smsTokenDetails =  a_prop.getProperty("sms.token.details");
            Config.pomUrl =  a_prop.getProperty("pom.api.url");
            Config.tcpIp =  a_prop.getProperty("tcp.ipaddress");
            Config.tcpPort =  a_prop.getProperty("tcp.portno");
            Config.surveyUrl =  a_prop.getProperty("survey.api.url");
            Config.acmUrl =  a_prop.getProperty("acm.api.url");
            Config.acmUsername=a_prop.getProperty("acm.api.username");
            Config.acmPassword=Decryption.getDecryptedData(a_prop.getProperty("acm.api.password"));
            Config.pomDisUrl =  a_prop.getProperty("pom.dis.api.url");
            Config.pomDisUsername=a_prop.getProperty("pom.dis.api.username");
            Config.pomDisPassword=Decryption.getDecryptedData(a_prop.getProperty("pom.dis.api.password"));
            
            Config.POMTOKENACCESSTIME=Integer.parseInt((String)a_prop.get("pom.tokenaccess.time"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.hikariDataSource = hikariDataSource;
    }
    
    @Autowired
    public void run(final ApplicationArguments args) throws SQLException {
        this.hikariDataSource.getConnection();
    }
    
    static {
        logger = LogManager.getLogger("Widget");
    }
}
