package auto.cache.annotation;

import java.lang.annotation.*;

/**
 * Created by Wang Jiacheng.
 * Date: 4/10/17
 * Time: 18:53
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    /**
     * 缓存的 key 的值
     * @return key 的值
     */
    String value();
}
