package auto.cache.annotation;

import java.lang.annotation.*;

/**
 * Created by Wang Jiacheng.
 * Date: 4/10/17
 * Time: 18:28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    String key();

    /**
     * <p>time to live as seconds.</p>
     * @return as seconds
     */
    long ttl() default -1L;

}
