package org.example.dto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UserDao {
    public boolean delete(Integer userId) {
        try (Connection connection = DriverManager.getConnection("url", "username", "password")){
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
