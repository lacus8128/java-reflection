/**
 *
 */
package account_keeping.common.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD
})

/**
 * DB処理のメソッドに付与するアノテーション
 *
 * メソッドに付けて、複数のSQLをまとめてトランザクション単位の処理とさせる
 */
public @interface Transactional {
    String version() default "1.0";
}
