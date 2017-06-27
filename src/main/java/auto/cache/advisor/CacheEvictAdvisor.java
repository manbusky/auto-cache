package auto.cache.advisor;

import com.zuche.tp.cache.annotation.CacheEvict;
import com.zuche.tp.cache.annotation.Param;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Wang Jiacheng.
 * Date: 4/11/17
 * Time: 10:31
 */
public class CacheEvictAdvisor extends AbstractCacheAdvisor {

    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        CacheEvict cache = method.getAnnotation(CacheEvict.class);

        Expression expression = parser.parseExpression(cache.key());

        StandardEvaluationContext context = new StandardEvaluationContext();

        List<Param> params = getCacheParameters(method);

        Object[] args = joinPoint.getArgs();

        for (int i = 0, size = params.size(); i < size; i++) {

            Param param = params.get(i);

            context.setVariable(param.value(), args[i]);
        }

        String key = expression.getValue(context, String.class);

        Object returnValue = null;

        switch (cache.moment()) {
            case BEFORE:

                cacheTemplate.evict(key);

                returnValue = joinPoint.proceed();

                break;
            case AFTER:

                returnValue = joinPoint.proceed();

                cacheTemplate.evict(key);

            break;
        }

        return returnValue;

    }
}
