package org.chen.aop;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.property.RedisPrefixProperties;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 接口防重复提交aop
 *
 * @author YuChen
 * @date 2018/12/20
 **/
@Slf4j
@Aspect
@Component
public class NoRepeatAop {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisPrefixProperties redisPrefixProperties;

    /**
     * 定义切面
     */
    @Pointcut("@annotation(org.chen.annotion.NoRepeat)")
    public void pointCut() {
    }

    /**
     * 切面前置处理
     */
    @Before("pointCut()")
    public void before() {
        // 获取防重复提交RedisKey
        String key = getNoRepeatKey();
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (StrUtil.isNotEmpty(key) && bucket.isExists()) {
            throw new BusinessException(BusinessExceptionEnum.REPEAT_REQUEST);
        }
        bucket.set("repeat",redisPrefixProperties.getNoRepeatExpireTime(),TimeUnit.MINUTES);
    }

    /**
     * 切面后置处理
     */
    @AfterReturning("pointCut()")
    public void afterReturning() {
        // 获取防重复提交RedisKey
        String key = getNoRepeatKey();
        if (StrUtil.isNotEmpty(key)) {
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.delete();
        }
    }

    /**
     * 切面后置处理
     */
    @AfterThrowing(pointcut = "pointCut()",throwing = "e")
    public void afterThrowing(Throwable e) {
        // 获取防重复提交RedisKey
        String key = getNoRepeatKey();
        if(e instanceof BusinessException ){
            BusinessException businessException = (BusinessException)e;
            Integer code = businessException.getCode();
            if(BusinessExceptionEnum.REPEAT_REQUEST.getCode().equals(code)){
                return;
            }
        }
        if (StrUtil.isNotEmpty(key)) {
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.delete();
        }
    }

    /**
     * 获取防重复提交RedisKey
     *
     * @return 防重复提交RedisKey
     * @author YuChen
     * @date 2018/12/20 21:42
     */
    private String getNoRepeatKey() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Long userId = (Long)request.getAttribute("userId");
        return redisPrefixProperties.getNoRepeatPrefix() + userId + request.getRequestURI();
    }

}
