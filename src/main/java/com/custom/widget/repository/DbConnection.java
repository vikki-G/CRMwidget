package com.custom.widget.repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DbConnection
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public Connection getConnection(final Logger log) {
        Connection connection = null;
        try {
            connection = this.jdbcTemplate.getDataSource().getConnection();
        }
        catch (SQLException e) {
            log.error("SQLException - Unable to connect the database : " + e.getMessage());
        }
        catch (Exception e2) {
            log.error("Exception - Unable to connect the database : " + e2.getMessage());
        }
        return connection;
    }
    
    public boolean closeConnection(final Connection connect, final ResultSet resultSet, final CallableStatement calStatement) throws SQLException {
        if (connect != null) {
            connect.close();
            if (resultSet != null) {
                resultSet.close();
            }
            if (calStatement != null) {
                calStatement.close();
            }
            return true;
        }
        return false;
    }
}
