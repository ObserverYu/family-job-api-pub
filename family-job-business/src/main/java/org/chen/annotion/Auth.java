package org.chen.annotion;

import java.lang.annotation.*;

/**
 * JWT验证的接口
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Auth {
}
