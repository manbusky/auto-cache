package auto.cache;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Wang Jiacheng.
 * Date: 4/12/17
 * Time: 14:01
 */
public class TypeResolver {

    /**
     * 获取集合元素类型
     * @param parameterizedType 类型
     * @return 集合元素类型
     */
    public static Class resolveCollectionItemType(ParameterizedType parameterizedType) {

        Type[] type = parameterizedType.getActualTypeArguments();

        if(type == null || type.length == 0) { return null; }

        return (Class)type[0];
    }

    /**
     * 获取 map 的 key 和 value 类型
     * @param parameterizedType 类型
     * @return key 和 value 类型
     */
    public static Pair<Class, Class> resolveMapKeyValueType(ParameterizedType parameterizedType) {

        Type[] type = parameterizedType.getActualTypeArguments();

        if(type == null || type.length < 2) { return null; }

        Class keyType = (Class)type[0];
        Class valueType = (Class)type[1];

        return Pair.of(keyType, valueType);

    }

}
