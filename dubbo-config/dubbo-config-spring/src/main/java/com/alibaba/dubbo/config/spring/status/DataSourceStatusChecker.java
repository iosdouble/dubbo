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
package com.alibaba.dubbo.config.spring.status;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.status.Status;
import com.alibaba.dubbo.common.status.StatusChecker;
import com.alibaba.dubbo.config.spring.ServiceBean;

/**
 * DataSourceStatusChecker
 * 对于数据源的状态检查
 * @author william.liangf
 */
@Activate
public class DataSourceStatusChecker implements StatusChecker {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceStatusChecker.class);

    @SuppressWarnings("unchecked")
    public Status check() {
        //获取到Provider的上下文
        ApplicationContext context = ServiceBean.getSpringContext();
        if (context == null) {
            //如果上下文内容为空，设置状态为未知状态
            return new Status(Status.Level.UNKNOWN);
        }
        //获取到数据源列表  DataSource 对象所表示的物理数据源的连接
        Map<String, DataSource> dataSources = context.getBeansOfType(DataSource.class, false, false);
        if (dataSources == null || dataSources.size() == 0) {
            return new Status(Status.Level.UNKNOWN);
        }
        //如果上下文和数据源都获取到则状态为成功状态
        Status.Level level = Status.Level.OK;
        StringBuilder buf = new StringBuilder();
        //获取到数据源实体
        for (Map.Entry<String, DataSource> entry : dataSources.entrySet()) {
            DataSource dataSource = entry.getValue();
            if (buf.length() > 0) {
                buf.append(", ");
            }
            buf.append(entry.getKey());
            try {
                //获取数据连接
                Connection connection = dataSource.getConnection();
                try {
                    //获取数据库元数据
                    DatabaseMetaData metaData = connection.getMetaData();
                    ResultSet resultSet = metaData.getTypeInfo();
                    try {
                        if (! resultSet.next()) {
                            level = Status.Level.ERROR;
                        }
                    } finally {
                        resultSet.close();
                    }
                    buf.append(metaData.getURL());
                    buf.append("(");
                    buf.append(metaData.getDatabaseProductName());
                    buf.append("-");
                    buf.append(metaData.getDatabaseProductVersion());
                    buf.append(")");
                } finally {
                    connection.close();
                }
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
                return new Status(level, e.getMessage());
            }
        }
        return new Status(level, buf.toString());
    }

}