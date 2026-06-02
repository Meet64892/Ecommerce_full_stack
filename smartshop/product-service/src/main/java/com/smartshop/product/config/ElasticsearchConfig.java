package com.smartshop.product.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ElasticsearchConfig - Elasticsearch low-level client configuration.
 *
 * <h2>Purpose</h2>
 * Provides explicit ES client wiring for search operations and future custom queries.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Inverted index: text terms map to documents for fast retrieval.</li>
 *   <li>Client transport: REST transport maps Java requests to ES HTTP APIs.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Supports ProductSearchRepository and potential advanced search services.
 *
 * @see com.smartshop.product.repository.ProductSearchRepository
 * @author SmartShop Team
 */
@Configuration
public class ElasticsearchConfig {

    /**
     * Creates native Elasticsearch client.
     *
     * @param host elasticsearch host
     * @param port elasticsearch port
     * @return configured client
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(@Value("${spring.elasticsearch.uris:http://localhost:9200}") final String host,
                                                   @Value("${elasticsearch.port:9200}") final int port) {
        final RestClient restClient = RestClient.builder(new HttpHost("localhost", port, "http")).build();
        final ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
