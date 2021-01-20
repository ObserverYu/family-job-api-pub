package org.chen.util.token;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.property.RedisPrefixProperties;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.concurrent.TimeUnit;

/**
 * redisToken相关工具类
 *
 * @author LiYuan
 * @date 2019/1/15
 **/
@Component
@Data
@Slf4j
public class TokenUtil {

    private RedissonClient redissonClient;

    String tokenPrefix;

    private JwtConsumer jwtConsumer;

    private String secret =  "12312";

    /**
     * 过期时间  单位：天
     *
     */
    private Long expireTime;

    private Key key;

    @Autowired
    public TokenUtil(RedissonClient redissonClient, RedisPrefixProperties redisPrefixProperties){
        this.redissonClient = redissonClient;
        this.tokenPrefix = redisPrefixProperties.getTokenPrefix();
        this.expireTime = redisPrefixProperties.getTokenExpireTime();
        try{
            this.key = new HmacKey(this.secret.getBytes("UTF-8"));
            this.jwtConsumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(30)
                    .setVerificationKey(key)
                    .setRelaxVerificationKeyValidation() // relaxes key length requirement
                    .build();
        }catch (Exception e){
            log.error("token秘钥初始化失败");
            throw new RuntimeException("token秘钥初始化失败");
        }

    }


    /**
     * 根据用户id生成token
     *
     * @param userId 用户id
     * @return token
     * @author LiYuan
     * @date 2019/1/15 20:32
     */
    public String generateToken(Long userId) {
        if(userId == null){
            throw new RuntimeException("userId不能为空");
        }
        String key = tokenPrefix + userId;
        RBucket<String> tokenBk = redissonClient.getBucket(key);
        // 生成token
        String token = createToken(userId);
        tokenBk.set(token,expireTime,TimeUnit.DAYS);
        return token;
    }

    //jws创建token
    public String createToken(Long userId) {
        JwtClaims claims = new JwtClaims();
        claims.setExpirationTimeMinutesInTheFuture(expireTime * 24 * 60);
        claims.setClaim("id",userId);
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(key);
        jws.setDoKeyValidation(false); // relaxes the key length requirement
        try{
            return jws.getCompactSerialization();
        }catch (Exception e){
            log.error("生成token失败");
            throw new RuntimeException("生成token失败");
        }
    }

    public JwtClaims consumerToken(String jwt){
        try {
            return jwtConsumer.processToClaims(jwt);
        } catch (InvalidJwtException e) {
            throw new BusinessException(BusinessExceptionEnum.TOKEN_ERROR);
        }
    }

    /**
     * 根据token获取用户id
     *
     * @param token token
     * @return 用户id
     * @author LiYuan
     * @date 2019/1/15 20:49
     */
    public Long checkToken(String token) {
        JwtClaims jwtClaims = consumerToken(token);
        Long userId;
        try {
             userId = jwtClaims.getClaimValue("id", Long.class);
        } catch (MalformedClaimException e) {
            throw new BusinessException(BusinessExceptionEnum.TOKEN_ERROR);
        }
        String key = tokenPrefix + userId;
        RBucket<String> redisTokenBk = redissonClient.getBucket(key);
        String redisToken = redisTokenBk.get();
        if (redisToken != null && !token.equals(redisToken)) {
            throw new BusinessException(BusinessExceptionEnum.TOKEN_ERROR);
        }
        // 重置该token过期时间为7天
        redisTokenBk.set(token,expireTime,TimeUnit.DAYS);
        return userId;
    }

    /**
     * 根据token获取用户id，不抛出异常
     *
     * @param token token
     * @return 用户id
     * @author LiYuan
     * @date 2019/1/15 20:49
     */
    public Long getUserIdFromTokenNoThrowException(String token) {
        String key = tokenPrefix + token;
        RBucket<Long> user = redissonClient.getBucket(key);
        return user.get();
    }

    /**
     * 获取登录用户id
     *
     * @return 登录用户id
     * @author LiYuan
     * @date 2018/11/14 15:03
     */
    public static Long getUserIdFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request.getAttribute("userId") == null) {
            return null;
        }
        return (Long)request.getAttribute("userId");
    }




}
