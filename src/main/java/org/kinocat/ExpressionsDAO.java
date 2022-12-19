package org.kinocat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpressionsDAO {
    static final String URL = "jdbc:mysql://localhost:3306";
    static final String USERNAME = "root";
    static final String PASSWORD = "";
    private static final String DB = "db_exp";
    private static final String TABLE = "expressions";

    private final Connection mConnection;

    public ExpressionsDAO() throws Exception {
        this(URL, USERNAME, PASSWORD);
    }

    public ExpressionsDAO(String url, String user, String password) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        mConnection = DriverManager.getConnection(url, user, password);
        createDbIfNeeded();
    }

    private void createDbIfNeeded() throws SQLException {
        Statement statement = mConnection.createStatement();
        statement.execute("CREATE DATABASE IF NOT EXISTS " + DB);
        statement.execute("USE " + DB);

        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + TABLE
                + " (id int NOT NULL AUTO_INCREMENT,"
                + " expression TEXT NOT NULL,"
                + " result DOUBLE NOT NULL,"
                + " PRIMARY KEY (id))";

        statement.execute(sqlCreate);
    }

    public void close() throws SQLException {
        mConnection.close();
    }

    public List<String> index(String key, char c, double value) throws SQLException {
        PreparedStatement statement;
        String sql = "SELECT * FROM " + TABLE;
        switch (c) {
            case '*':
                statement = mConnection.prepareStatement(sql);
                break;
            case '>':
            case '=':
            case '<':
                sql += " WHERE " + key + c + '?';
                statement = mConnection.prepareStatement(sql);
                statement.setDouble(1, value);
                break;
            default:
                throw new IllegalArgumentException();
        }
        ResultSet set = statement.executeQuery();
        List<String> list = new ArrayList<>();
        while (set.next()) {
            list.add(set.getInt("id") + ". " +
                    set.getString("expression") +
                    " = " +
                    Calculator.fmtDouble(set.getDouble("result")));
        }
        return list;
    }

    public void addExpressionAndResult(String expression, double result) throws SQLException {
        PreparedStatement statement = mConnection.prepareStatement("INSERT INTO " + TABLE + " VALUES (NULL, ?, ?)");
        statement.setString(1, expression);
        statement.setDouble(2, result);
        statement.executeUpdate();
    }

    public void update(int id, String expression, double result) throws SQLException {
        PreparedStatement statement = mConnection.prepareStatement("UPDATE " + TABLE + " SET expression=?, result=? WHERE id=?");
        statement.setString(1, expression);
        statement.setDouble(2, result);
        statement.setInt(3, id);
        statement.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement preparedStatement = mConnection.prepareStatement("DELETE FROM " + TABLE + " WHERE id=?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    public void truncate() throws SQLException {
        Statement statement = mConnection.createStatement();
        statement.executeUpdate("TRUNCATE " + TABLE);
    }
}
