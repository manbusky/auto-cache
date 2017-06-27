package auto.cache.advisor;

import auto.cache.annotation.Param;
import auto.cache.template.CacheTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang Jiacheng.
 * Date: 4/11/17
 * Time: 11:05
 */
public abstract class AbstractCacheAdvisor {

    protected static final ExpressionParser parser = new SpelExpressionParser();

    protected static final Logger logger = LoggerFactory.getLogger(AbstractCacheAdvisor.class);

    protected CacheTemplate cacheTemplate;

    protected List<Param> getCacheParameters(Method method) {

        Annotation annotations[][] = method.getParameterAnnotations();

        List<Param> parameters = new ArrayList<>();

        for (int i = 0, count = annotations.length; i < count; i++) {

            if(annotations[i].length == 0) {
                continue;
            }

            for (int j = 0, aCount = annotations[i].length; j < aCount; j++) {

                if(Param.class.equals(annotations[i][j].annotationType())) {

                    Param param = (Param)annotations[i][j];

                    parameters.add(param);
                }
            }
        }

        return parameters;
    }

    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }
}
