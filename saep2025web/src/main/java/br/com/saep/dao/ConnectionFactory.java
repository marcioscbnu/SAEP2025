package br.com.saep.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // Credenciais conforme o script SQL
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; 
    private static final String URL = "jdbc:mysql://localhost:3306/saep_db1?useTimezone=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "saep_db1"; 
    private static final String PASS = "saep_db1"; 

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER); 
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar o driver JDBC. Coloque o JAR do MySQL em WEB-INF/lib.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar. Verifique se o MySQL esta ativo e as credenciais corretas.", e);
        }
    }
    
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) { conn.close(); }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }
}