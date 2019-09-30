package hotpot.demo.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2019/7/30
 * @Author: qinzhu
 */
public class MysqlHelper {

    private static Connection connection;

    private static final String URL_TEMPLATE = "jdbc:mysql://%s:3306/%s?user=%s&password=%s&useUnicode=true&characterEncoding=UTF-8";

    private static final String IP = "127.0.0.1";

    private static final String DB_NAME = "testdb";

    private static final String USER = "root";

    private static final String PASSWORD = "panzer..";

    private static Connection getConnection() {
        if (connection == null) {
            synchronized (MysqlHelper.class) {
                if (connection == null) {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        connection = DriverManager.getConnection(String.format(URL_TEMPLATE, IP, DB_NAME, USER, PASSWORD));
                        return connection;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return connection;
    }


    public static boolean execute(String sql) {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            if (!(e instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException)) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static List<Object> selectOne(String sql) {
        sql = sql.toLowerCase();
        List<Object> result = new ArrayList<>();

        try {
            Statement statement = getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                int columnNumber = sql.substring(0, sql.indexOf("from")).split(",").length;
                for (int i = 0; i < columnNumber; i++) {
                    result.add(resultSet.getObject(i+1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
