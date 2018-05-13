/**
 *
 */
package account_keeping.common.service;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * サービスを呼び出す共通モジュール
 * 指定されたサービスを呼び出し、コントローラ（にあたる分岐処理）の機能も備える
 *
 * ※ サービスがもつメンバ変数は自動で読み取り、リフレクションで値をセットする
 */
public class ServiceInvoker {

    HttpServletRequest request;
    HttpServletResponse response;

    /**
     * コンストラクタ
     *
     * フィルタから、リクエストとレスポンスのオブジェクトを受け取る
     */
    public ServiceInvoker(HttpServletRequest req, HttpServletResponse res) {
        this.request = req;
        this.response = res;
    }

    /**
     * メインの処理
     * ※ 通常の処理と Ajax 用の処理を自動で切り替えられるようにする予定（Ajax 優先）
     *
     * 例外はServletException（サーブレット周り）以外は基本的にtry～catchする
     * @throws ServletException
     */
    public void invoke() throws ServletException {
        String servletPath = request.getServletPath();

        /* 最初にリクエストパラメータからService名を取得
         *
         */
        String serviceName = null;
        if(servletPath.charAt(0)=='/') {
            serviceName = servletPath.substring(1);
        } else {
            // ログをとる。不適切なパス。
        }

        /* "RequestMapping" というアノテーションを付与することで、直接Serviceクラス名を指定しないで
         * 別の論理名でServiceを呼び出せる仕組みを実装予定
         */

        /* リフレクションによりServiceのメタ情報を取得 */
        Class<?> serviceClazz = null;
        try {
            serviceClazz = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
        /* Serviceクラスがなければ、エラーを返す必要がある（未実装） */
            System.out.println(e.toString());
        }

        Service serviceInstance = null;
        try {
            serviceInstance = (Service) serviceClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.toString();
        }

        Field[] fields = serviceClazz.getFields();

        for(Field fElem : fields) {
            /* クラス情報から読み取ったメンバ変数に対応するsetterのメソッド名をリストに格納 */
            StringBuilder sb = new StringBuilder("set");
            String fName = fElem.getName();
            StringBuilder tail = new StringBuilder(fName).replace(0, 1, String.valueOf(fName.charAt(0)));
            String setterName = sb.append(tail).toString();

            String fTypeName = fElem.getType().getTypeName();

            /* ユーザーが画面から送信したリクエストパラメータを取得
             * 一旦、String型とint型、long型のみを想定する
             */
            String strParam = request.getParameter(setterName);

            /* setterはpublicの想定だが、一応 Accessible にしておく */
            if(strParam!=null) {
                Method setterMethod = null;
                try {
                    setterMethod = serviceClazz.getMethod(setterName);
                } catch (NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
                setterMethod.setAccessible(true);
                try {
                    if(fTypeName.equals("java.lang.String")) {
                        setterMethod.invoke(serviceInstance, strParam);
                    } else if(fTypeName.equals("int")) {
                        setterMethod.invoke(serviceInstance, Integer.parseInt(strParam));
                    } else if(fTypeName.equals("long")) {
                        setterMethod.invoke(serviceInstance, Long.parseLong(strParam));
                    } else {
                        System.out.println("Not Handlable Type");
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            } else {
                continue;
            }
        } // "fields" loop end

        /* 最後に、Service がもつ固有の処理を呼び出す */
        /* AJax に対応するかどうか、アノテーションで判定する
         * （Ajaxに対応しない場合のフォワード先とともに、設定ファイル化する形も今後検討していく）
         */
        String result = serviceInstance.execute();  // 結果としてのメッセージを受け取る（Struts2 風）
        System.out.println(result);

        Annotation[] annotations = serviceClazz.getAnnotations();
        boolean isAjaxHandler = false;
        for(Annotation annoElem : annotations) {
            String annoName = annoElem.getClass().getSimpleName();
            if(annoName.equals("AjaxHandler")) {
                isAjaxHandler = true;
                break;
            }
        }
        if(!isAjaxHandler) {
            /* 一旦はフォワード先をべた書きする  設定ファイルに切り出すなど後で改善する予定
             * （当面はこの分岐は使わないが、ハードコーディングは避けておきたい）
             */
            try {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            } catch (ServletException | IOException e) {
                throw new ServletException(e.toString());
            };
        } else {
            /* Ajaxに対応するServiceだった場合、レスポンスのオブジェクトをServiceで利用できるようにする
             * ただし、利用の際はsetterが必要
             */
            Method servletResSetter = null;
            try {
                servletResSetter = serviceClazz.getMethod("setServletResponse");
            } catch (NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
            servletResSetter.setAccessible(true);
            try {
                servletResSetter.invoke(serviceInstance, response);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
