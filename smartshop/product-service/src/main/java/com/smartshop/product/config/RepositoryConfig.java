package com.smartshop.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.smartshop.product.repository.jpa")
@EnableElasticsearchRepositories(basePackages = "com.smartshop.product.repository.search")
public class RepositoryConfig {
}
