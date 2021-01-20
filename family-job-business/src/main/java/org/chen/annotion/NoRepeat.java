package org.chen.annotion;

import java.lang.annotation.*;

/**
 * 接口防重复提交注解
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NoRepeat {
}
