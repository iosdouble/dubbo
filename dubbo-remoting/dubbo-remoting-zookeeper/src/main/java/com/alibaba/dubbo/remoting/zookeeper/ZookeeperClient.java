package com.alibaba.dubbo.remoting.zookeeper;

import java.util.List;

import com.alibaba.dubbo.common.URL;

/**
 * 对于Zookeeper的连接，实现了对应的Zookeeper的所有基本操作的封装
 * 支持了两种方式的Zookeeper连接方式
 * 第一种  通过原生的Zookeeper客户端去连接
 * 第二种  通过Curator连接Zookeeper客户端
 */
public interface ZookeeperClient {

	void create(String path, boolean ephemeral);

	void delete(String path);

	List<String> getChildren(String path);

	List<String> addChildListener(String path, ChildListener listener);

	void removeChildListener(String path, ChildListener listener);

	void addStateListener(StateListener listener);
	
	void removeStateListener(StateListener listener);

	boolean isConnected();

	void close();

	URL getUrl();

}
