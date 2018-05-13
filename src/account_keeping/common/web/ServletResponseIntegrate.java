/**
 *
 */
package account_keeping.common.web;

import javax.servlet.http.HttpServletResponse;

/**
 * サーブレットレスポンスのオブジェクトを取り込む Service に付与するインタフェース
 * Ajax を利用する際などに使う
 */
public interface ServletResponseIntegrate {
    void setServletResponse(HttpServletResponse response);
}
