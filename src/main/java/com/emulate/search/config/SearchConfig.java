package com.emulate.search.config;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;

@Configuration
public class SearchConfig {

    @Value("${es.hostName}")
    private String esHost;//master节点

    private String esHost2;//从属节点
    private String esHost3;//从属节点

    @Value("${es.transport}")
    private int esPort;//端口号

    @Value("${es.cluster.name}")
    private String esClusterName;


    /**解决由于与本项目中redis使用了netty而引起初始化时的错误
    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
    **/

    @Bean
    public TransportClient transportClient() throws Exception {

        Settings settings = Settings.builder()
                .put("cluster.name", esClusterName)
                .build();

        TransportClient transportClient = new PreBuiltTransportClient(settings);

                transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
        if (StringUtils.isNotEmpty(esHost2)) {
            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost2), esPort));
        }
        if (StringUtils.isNotEmpty(esHost3)) {
            transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost3), esPort));
        }
        return transportClient;
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(transportClient());
    }



}
