package com.arka.orden_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration

public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(){
        return  RestClient.builder();
    }

    @Bean("externalRestClientBuilder")
    public RestClient.Builder externalRestClientBuilder(){
        return RestClient.builder();
    }
}
