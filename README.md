
# Cache automatically with annotation

> Implement with `Spring AOP`

## get start

### 实现缓存相关的类

#### 实现  `auto.cache.template.CacheTemplate`

此类的作用是`Key/Value`、`Collection`、`Map`三种数据形式缓存的实现；例如：

```
public class RedisCacheTemplate implements CacheTemplate {

    private Jedis jedis = new Jedis("localhost");

    @Override
    public <T> void put(String key, T t) {

        jedis.set(key, ObjectMapperUtils.toJSON(t));
    }

    @Override
    public <T> void putCollection(String key, Collection<T> collection) {

        jedis.lpush(key, ObjectMapperUtils.toJSON(collection));
    }
    
    @Override
    public <K, V> void putMap(String key, Map<K, V> map) {

        Map<String, String> value = new HashMap<>();
        for (Map.Entry<K, V> entry: map.entrySet()) {

            K keyObject = entry.getKey();
            V valueObject = entry.getValue();

            value.put(ObjectMapperUtils.toJSON(keyObject), ObjectMapperUtils.toJSON(valueObject));
        }

        jedis.hmset(key, value);
    }

}
```

#### 实现 `auto.cache.template.CacheTemplateFactoryBean`

此类是`CacheTemplate`的`FactoryBean`，便于`spring`注入；例如实现如下：

```
public class RedisCacheTemplateFactoryBean implements CacheTemplateFactoryBean {

    @Override
    public CacheTemplate getObject() throws Exception {
        return new RedisCacheTemplate();
    }

    @Override
    public Class<?> getObjectType() {
        return RedisCacheTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
```

#### 配置文件

`spring`配置文件`applicationContext.xml`引入如下配置

- 引入位于`auto-cache`项目中`spring`配置文件

```
<import resource="applicationContext-cache.xml" />
```

- cacheTemplate 配置

```
<bean id="cacheTemplate" class="auto.cache.template.RedisCacheTemplateFactoryBean" />
```

- 加缓存Advisor配置，注入cacheTemplate

```
<bean id="cacheAdvisor" class="auto.cache.advisor.CacheAdvisor" parent="abstractCacheAdvisor">
	<property name="cacheTemplate" ref="cacheTemplate" />
</bean>
```

> 这里的`parent`的值是`abstractCacheAdvisor`，在第一步引入的`papplicationContext-cache.xml`已经定义好了；

- 剔除缓存Advisor配置，注入cacheTemplate

```
<bean id="cacheEvictAdvisor" class="auto.cache.advisor.CacheEvictAdvisor" parent="abstractCacheAdvisor">
	<property name="cacheTemplate" ref="cacheTemplate" />
</bean>
```

> 这里的`parent`的值是`abstractCacheAdvisor`，在第一步引入的`applicationContext-cache.xml`已经定义好了；

- 配置AOP

```
<aop:config>
    <aop:pointcut id="cachePointcut" expression="execution(* xxx.yyy.dao.*.*(..)) and @annotation(auto.cache.annotation.Cache)" />
    <aop:aspect ref="cacheAdvisor">
        <aop:around method="doAround" pointcut-ref="cachePointcut" />
    </aop:aspect>
    <aop:aspect ref="cacheEvictAdvisor">
    <aop:pointcut id="cacheEvictPointcut" expression="execution(* xxx.yyy.dao.*.*(..)) and @annotation(auto.cache.annotation.CacheEvict)" />
       <aop:around method="doAround" pointcut-ref="cacheEvictPointcut" />
    </aop:aspect>
</aop:config>
```

AOP的中的两个`pointcut`分别为`放置缓存`和`剔除缓存`做切入配置，注意对应的`annotation`；

### 配置注解

- `auto.cache.annotation.Cache` 是用来修饰缓存的方法；
- `auto.cache.annotation.CacheEvict` 是用来修饰剔除缓存的方法；

#### 加缓存

在需要加缓存的方法上加`@Cache`注解，例如：

```
@Cache(key = "'application_no_'.concat(#no)")
public Application findOneByNo(@CacheParam("no") String no) {
    …………
}
```

`@Cache`的`key`值是 [Spring Express Language](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html)，
`@CacheParam`用来修饰入参，用来构建`@Cache`的`key`所指定的表达式执行时候的变量上下文；

#### 剔除缓存，

在需要剔除的方法上加`@CacheEvict`注解，例如：

```
@CacheEvict(key = "'record_'.concat(#result.recordNo)")
public void modifyPayResult(@CacheParam("result") PayResult payResult) {
    …………
}
```

## 实现说明

### 存储数据格式

`@Cache`修饰的方法的返回数据类型，分为三个，返回的数据类型分三个：`普通JavaBean`、`Collection`、`Map`；

`auto-cache`会根据这三种不同的数据类型，调用`CacheTemplate`对应的方法来进行缓存；

#### 开发注意

`@Cache`修饰的方法的返回数据类型如果是`Collection`或者`Map`，一定要使用泛型进行数据类型修饰，例如：

```
@Cache(key = "'persons_age_'.concat(#age)")
public List<Person> findByAge(@CacheParam("age") Integer age) {
……
}
……
@Cache(key = "'persons_name_'.concat(#name)")
public Map<Long, Person> findByName(@CacheParam("name") String name) {
……
}
```

其中`List<Person>`和`Map<Long, Person>`都是分别使用泛型修饰过，以便`auto-cache`能根据类型将缓存中的数据进行反序列化；如果不这样做，缓存将失效！



### Have Fun
