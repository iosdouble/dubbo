/*
 * Copyright 1999-2011 Alibaba Group.
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
package com.alibaba.dubbo.remoting;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;
import com.alibaba.dubbo.remoting.transport.dispatcher.all.AllDispatcher;

/**
 * ChannelHandlerWrapper (SPI, Singleton, ThreadSafe)
 * 在使用Dubbo配置dispatcher是使用的参数有
 * all 所有消息都派发到线程池，包括请求，响应，连接事件，断开事件，心跳等。
 * direct 所有消息都不派发到线程池，全部在IO线程上直接执行。
 * message 只有请求响应消息派发到线程池，其它连接断开事件，心跳等消息，直接在IO线程上执行。
 * execution 只请求消息派发到线程池，不含响应，响应和其它连接断开事件，心跳等消息，直接在IO线程上执行。
 * connection 在IO线程上，将连接断开事件放入队列，有序逐个执行，其它消息派发到线程池
 *
 * @author chao.liuc
 */
@SPI(AllDispatcher.NAME)
public interface Dispatcher {

    /**
     * dispatch the message to threadpool.
     * 
     * @param handler
     * @param url
     * @return channel handler
     */
    @Adaptive({Constants.DISPATCHER_KEY, "dispather", "channel.handler"}) // 后两个参数为兼容旧配置
    ChannelHandler dispatch(ChannelHandler handler, URL url);

}