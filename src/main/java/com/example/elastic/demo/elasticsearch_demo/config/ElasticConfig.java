/*
 * @Author: wangran
 * 
 * @Date: 2020-04-01 14:54:04
 * 
 * @LastEditors: wangran
 * 
 * @LastEditTime: 2020-04-14 22:26:25
 */
package com.example.elastic.demo.elasticsearch_demo.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticConfig.class);

    @Value("${spring.data.elasticsearch.hosts}")
    private String hosts;

    @Value("${spring.data.elasticsearch.port}")
    private int port;

    @Value("${spring.data.elasticsearch.scheme}")
    private String scheme;

    @Value("${spring.data.elasticsearch.timeout}")
    private int timeout;

    @Bean(name = "highLevelClient")
    public RestHighLevelClient restHighLevelClient() {
        // 可以传httpHost数组
        String[] hostArray = hosts.split(",");
        logger.info("elasticsearch初始化配置开始");
        RestClientBuilder builder = RestClient.builder(new HttpHost(hostArray[0], port, scheme),
                new HttpHost(hostArray[1], port, scheme), new HttpHost(hostArray[2], port, scheme));
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            // 设置超时
            return requestConfigBuilder.setSocketTimeout(timeout);
        });
        logger.info("elasticsearch初始化配置完成");
        return new RestHighLevelClient(builder);
    }

}