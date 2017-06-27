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
public @interface CacheEvict {

    String key();

    CacheEvictMoment moment() default CacheEvictMoment.BEFORE;

}
