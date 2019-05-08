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
package com.alibaba.dubbo.rpc.protocol;

import java.util.Collections;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.ExporterListener;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.InvokerListener;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.listener.ListenerExporterWrapper;
import com.alibaba.dubbo.rpc.listener.ListenerInvokerWrapper;

/**
 * ListenerProtocol
 * 协议监听封装类
 * <p>
 * ExporterListener是远程服务发布监听器，可以监听服务发布和取消发布两个事件点；
 * <p>
 * InvokerListener是服务消费者引用调用器的监听器，可以监听引用和销毁两个事件方法
 * <p>
 * 支持可扩展的事件监听模型   例如适配器InvokerListenerAdapter、ExporterListenerAdapter以及简单的过期服务调用监听器DeprecatedInvokerListener
 *
 * @author william.liangf
 */
public class ProtocolListenerWrapper implements Protocol {

    private final Protocol protocol;

    public ProtocolListenerWrapper(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("protocol == null");
        }
        this.protocol = protocol;
    }

    //获取默认端口
    public int getDefaultPort() {
        return protocol.getDefaultPort();
    }

    /**
     * 暴露服务
     *
     * @param invoker 服务的执行体
     * @param <T>
     * @return
     * @throws RpcException
     */

    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        //如果注册的协议与执行者invoker，得到协议，将暴露服务
        //协议判断
        if (Constants.REGISTRY_PROTOCOL.equals(invoker.getUrl().getProtocol())) {
            return protocol.export(invoker);
        }
        //对于监听暴露的封装
        return new ListenerExporterWrapper<T>(protocol.export(invoker),
                //返回指定列表的不可修改视图（传入参数是一个列表对象）
                Collections.unmodifiableList(ExtensionLoader.getExtensionLoader(ExporterListener.class)
                        .getActivateExtension(invoker.getUrl(), Constants.EXPORTER_LISTENER_KEY)));
    }

    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        if (Constants.REGISTRY_PROTOCOL.equals(url.getProtocol())) {
            return protocol.refer(type, url);
        }
        return new ListenerInvokerWrapper<T>(protocol.refer(type, url),
                Collections.unmodifiableList(
                        ExtensionLoader.getExtensionLoader(InvokerListener.class)
                                .getActivateExtension(url, Constants.INVOKER_LISTENER_KEY)));
    }

    public void destroy() {
        protocol.destroy();
    }

}