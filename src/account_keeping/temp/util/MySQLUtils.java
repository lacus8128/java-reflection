/**
 *
 */
package account_keeping.temp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL を利用するためのクラス
 * ※ 継承させるための基底クラスにする形では過剰な制約を課すことになると判断し、
 *    適宜使用するツール的な位置付けにした
 */
public class MySQLUtils {

    private final String DRIVER_NAME = "com.jdbc.mysql.Driver";

    /**
     * MySQL に接続する処理
     * Bean の initメソッドから呼ばれる想定
     * ※ 今は DriverManager に拠って立つ仕組みだが、後に DataSource に以降予定
     */
    public Connection getConnection(String dbServerUrl, String dbName, String user, String password, String options) {
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbServerUrl + dbName + "?" + options, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
