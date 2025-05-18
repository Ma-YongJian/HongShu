package com.hongshu.common.config.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author xiaozhao
 */
@Configuration
@Slf4j
public class ESConfig {

    @Value("${es.url}")
    String esUrl;

    @Value("${es.port}")
    int esPort;

    @Value("${es.username}")
    String username;

    @Value("${es.password}")
    String password;

    private RestClient restClient;
    private ElasticsearchClient client;

    private ElasticsearchTransport transport;

    @Bean(name = "elasticsearchClient")
    public ElasticsearchClient getElasticsearchClient() {
        // 设置用户名和密码
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        // 创建带有基本身份验证的 RestClient
        restClient = RestClient.builder(
                        new HttpHost(esUrl, esPort)
                )
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();

        // 使用 Jackson 映射器创建传输层
        transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        // 创建 API 客户端
        client = new ElasticsearchClient(transport);
        return client;
    }


    public void close() {
        if (client != null) {
            try {
                transport.close();
                restClient.close();
            } catch (IOException e) {
                log.error("关闭es连接异常");
            }
        }
    }
}
