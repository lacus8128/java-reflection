/**
 *
 */
package account_keeping.common.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import account_keeping.common.service.ServiceInvoker;



/**
 * リクエストを途中で捕まえ、ServiceInvoker（役割：Interceptor）に処理を渡す
 *
 */
public class WebFilter implements Filter{

    private String encoding = null;


    @Override
    public void destroy() {
        encoding = null;
    }

    /**
     * doFilter メソッド
     * 主な処理はServiceInvokerに委譲して終了する
     */
    @Override
    public void doFilter(ServletRequest sReq, ServletResponse sRes, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sReq;
        HttpServletResponse res = (HttpServletResponse) sRes;

        sReq.setCharacterEncoding(encoding);
        sRes.setCharacterEncoding(encoding);

        /* 後の処理はServiceInvokerに任せる */
        new ServiceInvoker(req, res).invoke();
        // -----------------------------------------
        /* chain.doFilterは行わないことに注意 */

    }

    /**
     * init メソッド
     * 文字コードの設定のみ行う（値は web.xml から読み込む）
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("encoding");
    }

}
