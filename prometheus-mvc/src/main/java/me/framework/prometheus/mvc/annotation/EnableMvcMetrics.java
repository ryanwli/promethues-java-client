package me.framework.prometheus.mvc.annotation;

import java.lang.annotation.*;

/**
 * Created by ryan on 2018/3/21.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableMvcMetrics {
}
