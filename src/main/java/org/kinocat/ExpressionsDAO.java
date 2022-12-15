package org.kinocat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpressionsDAO {
    private static final String URL = "jdbc:mysql://localhost:3306";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String DB = "db_exp";
    private static final String TABLE = "expressions";

    private final Connection mConnection;

    public ExpressionsDAO() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        mConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        createDbIfNeeded();
    }

    private void createDbIfNeeded() throws SQLException {
        Statement statement = mConnection.createStatement();
        statement.execute("CREATE DATABASE IF NOT EXISTS " + DB);
        statement.execute("USE " + DB);
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + TABLE
                + "  (expression TEXT,"
                + "   result DOUBLE)";
        statement.execute(sqlCreate);
    }

    public void close() throws SQLException {
        mConnection.close();
    }

    public List<String> index(char c, double result) throws SQLException {
        PreparedStatement statement;
        String sql = "SELECT * FROM " + TABLE;
        switch (c) {
            case '*':
                statement = mConnection.prepareStatement(sql);
                break;
            case '>':
            case '=':
            case '<':
                sql += " WHERE result" + c + '?';
                statement = mConnection.prepareStatement(sql);
                statement.setDouble(1, result);
                break;
            default:
                throw new IllegalArgumentException();
        }
        ResultSet set = statement.executeQuery();
        List<String> list = new ArrayList<>();
        while (set.next()) {
            list.add(set.getString("expression") + " = " + set.getDouble("result"));
        }
        return list;
    }

    public void addExpressionAndResult(String expression, double result) throws SQLException {
        PreparedStatement statement = mConnection.prepareStatement("INSERT INTO " + TABLE + " VALUES (?, ?)");
        statement.setString(1, expression);
        statement.setDouble(2, result);
        statement.executeUpdate();
    }
}
