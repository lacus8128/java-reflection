/**
 *
 */
package account_keeping.common.service;

/**
 * すべての Service に付与するインタフェース（ポリモーフィズムが目的）
 */
public interface Service {
    /* Struts2 風に定数を宣言しておく（使用するかどうかは任意） */
    String SUCCESS = "success";
    String FAILURE = "failure";

    String execute();
}
