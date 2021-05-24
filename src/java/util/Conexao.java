package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection getConexao() throws SQLException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    //"jdbc:mysql:"
        } catch (ClassNotFoundException e) {
            throw new SQLException(e.getMessage());
        }
    }
 
    public static void main(String[] args) {
        try {
            getConexao();
            System.out.println("CONECTADO COM SUCESSO!");
        } catch (SQLException ex) {
            System.out.println("ERRO!");
            ex.printStackTrace();
        }
    }
}