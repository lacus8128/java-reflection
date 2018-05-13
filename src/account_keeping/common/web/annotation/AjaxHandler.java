/**
 *
 */
package account_keeping.common.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(
    ElementType.TYPE
)

/**
 * Ajax に対応する Service に付与するアノテーション
 */
public @interface AjaxHandler {
    String version() default "1.0";
}
