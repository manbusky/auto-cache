package auto.cache.advisor;

import auto.cache.TypeResolver;
import auto.cache.annotation.Cache;
import auto.cache.annotation.Param;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang Jiacheng.
 * Date: 4/10/17
 * Time: 18:37
 */
public class CacheAdvisor extends AbstractCacheAdvisor {

    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        if(Void.TYPE.equals(method.getReturnType())) {

            return joinPoint.proceed();
        }

        Cache cache = method.getAnnotation(Cache.class);

        Expression expression = parser.parseExpression(cache.key());

        StandardEvaluationContext context = new StandardEvaluationContext();

        List<Param> params = getCacheParameters(method);

        Object[] args = joinPoint.getArgs();

        for (int i = 0, size = params.size(); i < size; i++) {

            Param param = params.get(i);

            context.setVariable(param.value(), args[i]);
        }

        String key = expression.getValue(context, String.class);

        Class<?> returnType = method.getReturnType();

        if (Collection.class.isAssignableFrom(returnType)) {

            ParameterizedType parameterizedType = (ParameterizedType)method.getGenericReturnType();

            Class itemType = TypeResolver.resolveCollectionItemType(parameterizedType);

            if(itemType == null) {

                logger.warn("Not found generic type in return type of method '{}'. WILL DO NOT CACHE.", method.toGenericString());

                return joinPoint.proceed();
            }

            Collection collection = cacheTemplate.getCollection(key, itemType);

            if(CollectionUtils.isEmpty(collection)) {

                collection = (Collection) joinPoint.proceed();

                if(CollectionUtils.isNotEmpty(collection)) {

                    cacheTemplate.putCollection(key, collection, cache.ttl());
                }
            }

            return collection;

        } else if(Map.class.isAssignableFrom(returnType)) {

            ParameterizedType parameterizedType = (ParameterizedType)method.getGenericReturnType();

            Pair<Class, Class> pair = TypeResolver.resolveMapKeyValueType(parameterizedType);

            if(pair == null) {

                logger.warn("Not found generic type in return type of method '{}'. WILL DO NOT CACHE.", method.toGenericString());

                return joinPoint.proceed();
            }

            Map map = cacheTemplate.getMap(key, pair.getKey(), pair.getValue());

            if(MapUtils.isEmpty(map)) {

                map = (Map)joinPoint.proceed();

                if(MapUtils.isNotEmpty(map)) {

                    cacheTemplate.putMap(key, map, cache.ttl());
                }
            }

            return map;

        } else {

            Object value = cacheTemplate.get(key, method.getReturnType());

            if(value == null) {

                value = joinPoint.proceed();

                if(value != null) {

                    cacheTemplate.put(key, value, cache.ttl());
                }
            }

            return value;
        }
    }

}
