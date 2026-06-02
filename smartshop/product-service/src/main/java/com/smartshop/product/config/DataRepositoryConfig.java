package com.smartshop.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Splits JPA and Elasticsearch repository scanning so interfaces are not registered twice.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.smartshop.product.repository.jpa")
@EnableElasticsearchRepositories(basePackages = "com.smartshop.product.repository.search")
public class DataRepositoryConfig {
}
