/**
 *
 */
package account_keeping.temp.bean;

import java.util.Map;
import java.util.List;

/**
 * データを（検索して）取得する処理を担うモジュールのインタフェース
 */
public interface DataSearcher {

    /**
     * 初期化の際に行う処理
     */
    public abstract void init();

    /**
     * DB に対して検索を実行する処理
     */
    public abstract <T> List<T> search(Map<String, String> nameTable, Class<T> dtoTypeReference);

}
