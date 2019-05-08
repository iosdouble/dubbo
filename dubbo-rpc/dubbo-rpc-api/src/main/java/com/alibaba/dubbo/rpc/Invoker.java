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
package com.alibaba.dubbo.rpc;

import com.alibaba.dubbo.common.Node;

/**
 * Invoker. (API/SPI, Prototype, ThreadSafe)
 * <p>
 * 服务执行体
 * 有获取服务发布的URL
 * <p>
 * 服务发布流程
 * 首先ServiceConfig类拿到对外提供服务的实际类ref(如：HelloWorldImpl),然后通过ProxyFactory类的getInvoker方
 * 法使用ref生成一个AbstractProxyInvoker实例，到这一步就完成具体服务到Invoker的转化。接下来就是Invoker转换
 * 到Exporter的过程。Dubbo处理服务暴露的关键就在Invoker转换到Exporter的过程
 * <p>
 * <p>
 * 服务消费的主过程：
 * 首先ReferenceConfig类的init方法调用Protocol的refer方法生成Invoker实例，这是服务消费的
 * 关键。接下来把Invoker转换为客户端需要的接口(如：HelloWorld)。
 * <p>
 * <p>
 * 关于每种协议如RMI/Dubbo/Web service等它们在调用refer方法生成Invoker实例的细节可以通过具体代码查看
 *
 * @author william.liangf
 * @see com.alibaba.dubbo.rpc.Protocol#refer(Class, com.alibaba.dubbo.common.URL)
 * @see com.alibaba.dubbo.rpc.InvokerListener
 * @see com.alibaba.dubbo.rpc.protocol.AbstractInvoker
 */
public interface Invoker<T> extends Node {

    /**
     * get service interface.
     * 获取服务接口
     *
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     * 服务执行核心方法
     *
     * @param invocation 调用信息Invocation
     * @return result
     * @throws RpcException
     */
    Result invoke(Invocation invocation) throws RpcException;

}