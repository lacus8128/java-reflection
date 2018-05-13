/**
 *
 */
package account_keeping.temp.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;


import account_keeping.common.configuration.ApplicationContext;
import account_keeping.common.configuration.XMLApplicationContext;
import account_keeping.common.service.Service;
import account_keeping.common.web.ServletResponseIntegrate;
import account_keeping.common.web.annotation.AjaxHandler;
import account_keeping.temp.bean.DataSearcher;
import account_keeping.temp.bean.DataSearcherMySQLImpl;
import account_keeping.temp.dto.ExpenseRecordDTO;

/**
 * 固有のService Class
 * 画面（一覧表示）に送るデータを取得する
 *
 * あえて Ajaxに対応させる
 */
@AjaxHandler
public class DataSelectService implements Service, ServletResponseIntegrate {

    HttpServletResponse response = null;

    /**
     * メインの処理
     */
    @Override
    public String execute() {
        String result = SUCCESS;

        /* 使用する Java Bean の呼び出し（DIコンテナ の利用） */
        ApplicationContext appContext = new XMLApplicationContext();  // ここではXMLから呼び出す仕組みを実装
        DataSearcher ds = appContext.getBean(DataSearcherMySQLImpl.class);  // ダミーのスタブと差し替えられる

        /* 一旦、全件検索（上限30件）の想定とする
         * （拡張は後で考える）
         */
        /* DTO のメンバの名とDBのカラム物理名の対応表のマップ（命名規則が違う場合に違いを吸収する）。
         * このマップは手書きする必要がある
         */
        Map<String, String> nameTable = new HashMap<String, String>();
        nameTable.put("id", "id");
        nameTable.put("place", "place");
        nameTable.put("product", "product");
        nameTable.put("amount", "amount");  // nameTable.put("totalAmount", "total_amount"); となったかも知れない
        nameTable.put("description", "description");

        /* DB からのデータ取得 */
        List<ExpenseRecordDTO> expenseList = ds.search(nameTable, ExpenseRecordDTO.class);

        JSONArray jArray = new JSONArray();

        for(ExpenseRecordDTO dto : expenseList) {

            JSONObject jObj = new JSONObject();
            jObj.put("id", dto.getId());
            jObj.put("place", dto.getPlace());
            jObj.put("price", dto.getProduct());
            jObj.put("amount", dto.getAmount());
            jObj.put("description", dto.getDescription());

            jArray.put(jObj);  // JSONObject の追加
        }

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
            result = FAILURE;
        }

        writer.print(jArray.toString());
        writer.close();

        return result;
    }

    /**
     * サーブレットレスポンスを取り込むための setter
     * 呼び出しは自動で（ServiceInvokerによって）行われる
     */
    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }


}
