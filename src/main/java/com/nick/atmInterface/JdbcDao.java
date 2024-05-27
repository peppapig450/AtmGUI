package com.nick.atmInterface;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.PropertyPermission;

public class JdbcDao {

    private static final String DATABASE_URL = "jdbc:mysql://(host=localhost,port=3306,user=nick_school,password=Nb!002088)/atm";
    private static final String INSERT_QUERY = "INSERT INTO users (full_name, user_name, password, pin) VALUES (?, ?, ?, ?)";

    public void addBankAccount(String username, String accNumber) {
        final String getData = "select user_id, pin from users "
                + "where user_name = ?";
        final String updateAccount = "insert into account (accountNumber, accountBalance, userID, pin) VALUES (?, 0, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement dataGet = conn.prepareStatement(getData);
            dataGet.setString(1, username);

            ResultSet rs = dataGet.executeQuery();

            PreparedStatement ps = conn.prepareStatement(updateAccount);

            ps.setString(1, accNumber);
            while (rs.next()) {
                ps.setInt(2, rs.getInt("user_id"));
                ps.setString(3, rs.getString("pin"));
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

    public int getAccountPin(String username) {
        final String sql = "select pin from account "
                + "where userID = ?";
        int pin = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement ps = conn.prepareStatement(sql);
            int userID = getUserIDFromUser(username);
            ps.setInt(1, userID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pin = rs.getInt("pin");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return pin;
    }

    public String getFullNameFromUser(String username) {
        final String sql = "select full_name from users "
                + "where user_name = ?";
        String fullName = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                fullName = rs.getString("full_name");
            }

        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return fullName;
    }

    public int getUserIDFromUser(String username) {
        final String sql = "select user_id from users "
                + "where user_name = ?";

        int userID = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userID = rs.getInt("user_id");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }

        return userID;
    }

    public int getAccountBalance(int userID) {
        int balance = 0;

        final String sql = "SELECT accountBalance FROM account "
                + "WHERE userID = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                balance = rs.getInt("accountBalance");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return balance;
    }

    public void depositAmount(int amount, int accID) {
        final String sql = "update account"
                + " set accountBalance = accountBalance + ?"
                + " where userID = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, amount);
            ps.setInt(2, accID);

            ps.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

    public void withdrawAmount(int amount, int accID) {
        final String sql = "update account"
                + " set accountBalance = accountBalance - ?"
                + " where userID = ?"
                + " and accountBalance >= ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, amount);
            ps.setInt(2, accID);
            ps.setInt(3, amount);

            ps.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

    public int getUserIDFromAccountNumber(String accountNum) {
        final String sql = "select userID from account"
                + " where accountNumber = ?";
        int userID = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, accountNum);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userID = rs.getInt("userID");
            }
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return userID;
    }

    public String getUsernamePass(String username) {
        String pass = "";

        final String sql = "SELECT password FROM users "
                + "WHERE user_name = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pass = rs.getString("password");

            }

        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return pass;
    }

    public void registerUser(String fullName, String userName, String password, String pin) {
        // estabilish connection

        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DATABASE_URL);

            // create statement with connection object
            PreparedStatement preparedStatement = connection.prepareStatement((INSERT_QUERY));
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, userName);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, pin);

            // execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }

    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}