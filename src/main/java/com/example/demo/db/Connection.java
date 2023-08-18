package com.example.demo.db;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {

    public java.sql.Connection connect() {

        final String url = "jdbc:postgresql://localhost/Tenpo";
        final String user = "postgres";
        final String password = "admin";
        java.sql.Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (
                SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
}
