/**
 *
 */
package account_keeping.common.configuration;

/**
 * DI（依存性の注入）の実現のためのインタフェース
 * これによって柔軟に疎結合を実現できるようになる
 *
 */
public interface ApplicationContext {

    /**
     * Service の中で使用したい Java Bean を、クラスリテラルを指定して呼び出す
     * @param beanClazz
     * @return T
     */
    <T> T getBean(Class<T> beanClazz);
}
