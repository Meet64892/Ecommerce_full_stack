package com.smartshop.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * ElasticsearchConfig - Configures the Elasticsearch Java client.
 *
 * <h2>Purpose</h2>
 * Product search uses Elasticsearch alongside PostgreSQL because full-text search needs analyzers, relevance scoring,
 * and inverted indexes that relational databases are not optimized to provide at catalog scale.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Shards: Split an index into pieces so data can scale across nodes.</li>
 *   <li>Replicas: Copies of shards that improve read availability and resilience.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Spring Data Elasticsearch uses this configuration to create the client used by ProductSearchRepository.
 *
 * @see com.smartshop.product.repository.ProductSearchRepository
 * @author SmartShop Team
 */
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    private final String hostAndPort;

    /**
     * Captures the configured Elasticsearch host.
     *
     * @param hostAndPort host:port string for the local or remote cluster
     */
    public ElasticsearchConfig(@Value("${spring.elasticsearch.uris:http://localhost:9200}") String uris) {
        String firstUri = uris.split(",")[0].trim();
        this.hostAndPort = firstUri.replace("http://", "").replace("https://", "");
    }

    /**
     * Builds the client configuration used by Spring Data.
     *
     * @return Elasticsearch client configuration
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder().connectedTo(hostAndPort).build();
    }
}
