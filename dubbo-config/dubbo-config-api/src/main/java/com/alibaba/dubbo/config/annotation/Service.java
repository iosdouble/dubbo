/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Service
 * 服务注解配置
 * @author william.liangf
 * @export
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Service {

    //接口类
    Class<?> interfaceClass() default void.class;

    //接口名称
    String interfaceName() default "";

    //版本
    String version() default "";

    //组信息
    String group() default "";

    //路径信息
    String path() default "";

    //是否暴露
    boolean export() default false;

    //token
    String token() default "";

    //是否赞成使用
    boolean deprecated() default false;

    //是否动态
    boolean dynamic() default false;

    String accesslog() default "";

    int executes() default 0;

    boolean register() default false;
    //权重
    int weight() default 0;
    //文档说明
    String document() default "";

    int delay() default 0;

    String local() default "";

    String stub() default "";

    String cluster() default "";

    String proxy() default "";

    int connections() default 0;

    int callbacks() default 0;

    String onconnect() default "";

    String ondisconnect() default "";

    String owner() default "";

    String layer() default "";

    int retries() default 0;

    String loadbalance() default "";

    boolean async() default false;

    int actives() default 0;

    boolean sent() default false;

    String mock() default "";

    String validation() default "";

    int timeout() default 0;

    String cache() default "";

    String[] filter() default {};

    String[] listener() default {};

    String[] parameters() default {};

    String application() default "";

    String module() default "";

    String provider() default "";

    String[] protocol() default {};

    String monitor() default "";

    String[] registry() default {};

}
