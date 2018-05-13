/**
 *
 */
package account_keeping.temp.bean;

import java.util.Map;
import java.util.List;

/**
 * DBから
 */
public class DataSearcherDummyImpl implements DataSearcher {

    @Override
    public void init() {
        // 特に処理は必要ない
    }

    @Override
    public <T> List<T> search(Map<String, String> nameTable, Class<T> dtoTypeReference) {
        /* ここにダミーデータの入ったリストを作成する処理を書く */
        return null;
    }

}
