package auto.cache.template;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Wang Jiacheng.
 * Date: 4/12/17
 * Time: 10:43
 */
public interface CacheTemplate {

    /**
     * 缓存
     * @param key key
     * @param t 对象
     */
    <T> void put(String key, T t);

    /**
     * 缓存
     * @param key key
     * @param t 对象
     * @param ttl time to live
     */
    <T> void put(String key, T t, long ttl);

    /**
     * 缓存
     * @param key key
     * @param collection 集合
     */
    <T> void putCollection(String key, Collection<T> collection);

    /**
     * 缓存
     * @param key key
     * @param collection 集合
     * @param ttl time to live
     */
    <T> void putCollection(String key, Collection<T> collection, long ttl);

    /**
     * 缓存 map
     * @param key key
     * @param map map
     */
    <K, V> void putMap(String key, Map<K, V> map);

    /**
     * 缓存 map
     * @param key key
     * @param map map
     * @param ttl time to live
     */
    <K, V> void putMap(String key, Map<K, V> map, long ttl);

    /**
     * 获取
     * @param key key
     * @return 缓存值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 获取集合
     * @param key key
     * @param itemClazz class of item
     * @return 缓存集合
     */
    <T> Collection<T> getCollection(String key, Class<T> itemClazz);

    /**
     * 获取 map
     * @param key key
     * @param keyType key类 型
     * @param valueType value 类型
     * @return 缓存 map
     */
    <K, V> Map<K, V> getMap(String key, Class keyType, Class valueType);

    /**
     * 剔除缓存
     * @param key key
     */
    void evict(String key);

}
