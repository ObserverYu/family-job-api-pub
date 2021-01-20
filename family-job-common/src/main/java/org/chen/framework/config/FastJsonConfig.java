package org.chen.framework.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.math.BigDecimal;

/**
 * fastJson相关配置类，统一处理时间，BigDecimal四舍五入保留两位小数再转字符串
 *
 * @author LiYuan
 * @date 2019/4/19
 **/
@Configuration
public class FastJsonConfig {
    @Bean
    public HttpMessageConverters httpMessageConverters() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        com.alibaba.fastjson.support.config.FastJsonConfig fastJsonConfig = new com.alibaba.fastjson.support.config.FastJsonConfig();
        // 处理FastJson序列化依次为（消除对同一对象循环引用的问题，输出值为null的字段，List字段如果为null,输出为[],而非null，字符类型字段如果为null,输出为”“,而非null）
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty);
        // 转换时间格式
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializeFilters((ValueFilter) (object, name, value) -> {
            if (value != null && value instanceof BigDecimal) {
                // BigDecimal四舍五入保留两位小数再转字符串
                return ((BigDecimal) value).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
            }
            return value;
        });

        fastConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters((HttpMessageConverter<?>) fastConverter);
    }
}
