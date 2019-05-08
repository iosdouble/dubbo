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

package com.alibaba.dubbo.rpc.protocol;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * AbstractProxyProtocol
 * 抽象代协议代理
 *
 * @author william.liangf
 */
public abstract class AbstractProxyProtocol extends AbstractProtocol {

    //CopyOnWriteArrayList支持多线程读写操作
    private final List<Class<?>> rpcExceptions = new CopyOnWriteArrayList<Class<?>>();
    ;

    //代理工厂
    private ProxyFactory proxyFactory;

    public AbstractProxyProtocol() {
    }

    public AbstractProxyProtocol(Class<?>... exceptions) {
        for (Class<?> exception : exceptions) {
            addRpcException(exception);
        }
    }

    public void addRpcException(Class<?> exception) {
        this.rpcExceptions.add(exception);
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> Exporter<T> export(final Invoker<T> invoker) throws RpcException {
        //获取URI对应的serviceKey值
        final String uri = serviceKey(invoker.getUrl());
        //根据uri获取对应的exporter(接出者)
        Exporter<T> exporter = (Exporter<T>) exporterMap.get(uri);
        //如果对应的的接出协议存在在直接返回，实现接口支持幂等调用。
        if (exporter != null) {
            return exporter;
        }
        //执行方法暴露服务
        final Runnable runnable = doExport(proxyFactory.getProxy(invoker), invoker.getInterface(), invoker.getUrl());
        //调用proxyFactory.getProxy(invoker)来获取到对应的invoker的代理对象。
        exporter = new AbstractExporter<T>(invoker) {
            public void unexport() {
                super.unexport();
                exporterMap.remove(uri);
                if (runnable != null) {
                    try {
                        runnable.run();
                    } catch (Throwable t) {
                        logger.warn(t.getMessage(), t);
                    }
                }
            }
        };
        exporterMap.put(uri, exporter);
        return exporter;
    }

    public <T> Invoker<T> refer(final Class<T> type, final URL url) throws RpcException {
        //先调用doRefer获得服务对象，再调用proxyFactory.getInvoker方法获取到对应的invoker对象
        final Invoker<T> tagert = proxyFactory.getInvoker(doRefer(type, url), type, url);
        Invoker<T> invoker = new AbstractInvoker<T>(type, url) {
            @Override
            protected Result doInvoke(Invocation invocation) throws Throwable {
                try {
                    Result result = tagert.invoke(invocation);
                    Throwable e = result.getException();
                    if (e != null) {
                        for (Class<?> rpcException : rpcExceptions) {
                            if (rpcException.isAssignableFrom(e.getClass())) {
                                throw getRpcException(type, url, invocation, e);
                            }
                        }
                    }
                    return result;
                } catch (RpcException e) {
                    if (e.getCode() == RpcException.UNKNOWN_EXCEPTION) {
                        e.setCode(getErrorCode(e.getCause()));
                    }
                    throw e;
                } catch (Throwable e) {
                    throw getRpcException(type, url, invocation, e);
                }
            }
        };
        invokers.add(invoker);
        return invoker;
    }

    protected RpcException getRpcException(Class<?> type, URL url, Invocation invocation, Throwable e) {
        RpcException re = new RpcException("Failed to invoke remote service: " + type + ", method: "
                + invocation.getMethodName() + ", cause: " + e.getMessage(), e);
        re.setCode(getErrorCode(e));
        return re;
    }

    protected int getErrorCode(Throwable e) {
        return RpcException.UNKNOWN_EXCEPTION;
    }

    /**
     * 留给子类实现的真正将类发布到URL的抽象方法定义，根据具体的协议来实现
     *
     * @param impl 泛型接口
     * @param type 泛型类型
     * @param url
     * @param <T>  泛型
     * @return
     * @throws RpcException
     */
    protected abstract <T> Runnable doExport(T impl, Class<T> type, URL url) throws RpcException;

    /**
     * 留给子类实现的远程服务的抽象方法定义，该方法是将URL和type接口类应用到一个可以远程调用代理对象
     *
     * @param type
     * @param url
     * @param <T>
     * @return
     * @throws RpcException
     */
    protected abstract <T> T doRefer(Class<T> type, URL url) throws RpcException;

}
