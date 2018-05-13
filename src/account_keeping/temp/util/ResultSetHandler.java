/**
 *
 */
package account_keeping.temp.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 結果セットを扱う機能を提供するクラス
 */
public class ResultSetHandler {

    /**
     * DTO に値を詰める処理
     * （汎用性を高めようとしたが、やり過ぎの可能性もある）
     * （日付の扱い方によっては、かえって不都合な制約を生みかねないか）
     *
     * 例によって、一旦は String と int、long のみを考慮する（のちに日付等も扱えるよう実装する）
     * @param rs
     * @param dtoTypeReference
     */
    public static <T> List<T> pack(ResultSet rs, Map<String, String> nameTable, Class<T> dtoTypeReference) {
        List<T> list = new ArrayList<T>();
        try {
            while(rs.next()) {
                /* DTO のインスタンス生成 */
                T dto = null;
                try {
                    dto = dtoTypeReference.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                Field[] fields = dtoTypeReference.getFields();
                for(Field fElem : fields) {
                    String fName = fElem.getName();
                    String fTypeName = fElem.getType().getTypeName();
                    String setterName = getSetterName(fName);

                    /* ResultSet から値を取得して DTO に格納する */
                    try {
                        Method setterMethod = dtoTypeReference.getMethod(setterName);
                        setterMethod.setAccessible(true);
                        if(fTypeName.equals("java.lang.String")) {
                            if(rs.getString(nameTable.get(fName)) != null) {
                                setterMethod.invoke(dto, rs.getString(nameTable.get(fName)));
                            } else {
                                setterMethod.invoke(dto, "");  // 空文字をセットする
                            }
                        } else if(fTypeName.equals("int")) {
                            /* レコード内の値が NULL の時に空文字をセットできないため、
                             * getInt や getLong を使うこちらの分岐は今後使わないようにする想定
                             */
                            if(rs.getString(nameTable.get(fName)) != null) {
                                setterMethod.invoke(dto, rs.getInt(nameTable.get(fName)));
                            } else {
                                setterMethod.invoke(dto, 0);
                            }
                        } else if(fTypeName.equals("long")) {
                            if(rs.getString(nameTable.get(fName)) != null) {
                                setterMethod.invoke(dto, rs.getLong(nameTable.get(fName)));
                            } else {
                                setterMethod.invoke(dto, 0);
                            }
                        }
                    } catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                    }
                } // fields loop end

                list.add(dto);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * メンバ変数名からsetter名を生成する関数
     * （小規模な文字列処理）
     *
     * @param fieldName
     * @return setterName
     */
    public static String getSetterName(String fieldName) {
        StringBuilder sb = new StringBuilder("set");
        StringBuilder tail = new StringBuilder(fieldName).replace(0, 1, String.valueOf(fieldName.charAt(0)).toUpperCase());
        return sb.append(tail).toString();
    }
}
