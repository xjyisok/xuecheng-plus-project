package com.xuecheng.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.hostlist}")
    private String hostlist;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 解析 hostlist 配置信息
        String[] split = hostlist.split(",");
        HttpHost[] httpHosts = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String[] hostPort = split[i].split(":");
            httpHosts[i] = new HttpHost(hostPort[0], Integer.parseInt(hostPort[1]), "http");
        }

        // 创建 RestClient
        RestClient restClient = RestClient.builder(httpHosts).build();

        // 创建 ElasticsearchTransport
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // 创建 ElasticsearchClient
        return new ElasticsearchClient(transport);
    }
}
