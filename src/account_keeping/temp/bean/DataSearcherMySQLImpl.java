/**
 *
 */
package account_keeping.temp.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import account_keeping.temp.util.MySQLUtils;
import account_keeping.temp.util.ResultSetHandler;

/**
 * MySQL 使用のデータ取得処理の実装
 */
public class DataSearcherMySQLImpl implements DataSearcher {

    private Connection conn = null;

    private String dbServerUrl;
    private String dbName;
    private String user;
    private String password;
    private String options;

    /**
     * init メソッドの実装 （本フレームワークでは Bean は皆 init メソッドを実装する）
     * 本メソッドは ApplicationContext のクラスがもつ getBeanメソッドによって自動的に呼び出される
     */
    @Override
    public void init() {
        MySQLUtils mysql = new MySQLUtils();
        this.conn = mysql.getConnection(dbServerUrl, dbName, user, password, options);
    }

    /**
     * 具体的な検索処理の実装
     * このメソッドにも、ResultSetHandler と同様に Type Reference 引数を設定する予定
     *
     * ※ 今回は単一テーブルを想定しているとはいえ、DBのテーブルに依存する実装であることに注意すべきであった
     */
    @Override
    public <T> List<T> search(Map<String, String> nameTable, Class<T> dtoTypeReference) {
        List<T> expenseList = new ArrayList<T>();

        String sql = "SELECT * FROM expense LIMIT 30";  // プレースホルダなし
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            expenseList = (List<T>) ResultSetHandler.pack(rs, nameTable, dtoTypeReference);
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            /* closeをどのように処理するか（try-with-resourcesは使えるか等）は考える余地あり
             * 今後の課題としては、その他、コネクションプール等
             */
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return expenseList;
    }

    public String getDbServerUrl() {
        return dbServerUrl;
    }
    public void setDbServerUrl(String dbServerUrl) {
        this.dbServerUrl = dbServerUrl;
    }
    public String getDbName() {
        return dbName;
    }
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getOptions() {
        return options;
    }
    public void setOptions(String options) {
        this.options = options;
    }

}
