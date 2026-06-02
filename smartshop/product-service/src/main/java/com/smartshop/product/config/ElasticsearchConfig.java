package com.smartshop.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * ElasticsearchConfig - Connection configuration for the Elasticsearch client.
 *
 * <h2>Purpose</h2>
 * Tells Spring Data Elasticsearch where the ES cluster lives. Extending
 * {@link ElasticsearchConfiguration} gives us a typed place to build the
 * {@link ClientConfiguration} (host, ssl, auth) rather than relying solely on
 * properties.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>The host is externalized so dev (localhost) and prod (cluster DNS)
 *       differ only by configuration, not code.</li>
 *   <li>In dev we connect to a single-node cluster with security disabled; in
 *       prod you would enable TLS + credentials here.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Supplies the client used by {@code ProductSearchRepository} and
 * {@code ProductSearchService}.
 *
 * @author SmartShop Team
 */
@Configuration
@ConfigurationProperties(prefix = "smartshop.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    /** host:port of the ES node, bound from smartshop.elasticsearch.uris. */
    private String uris = "localhost:9200";

    /**
     * Builds the client configuration pointing at the configured ES node.
     *
     * @return the {@link ClientConfiguration} used to create the ES client
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                // connectedTo accepts host:port (no scheme). Security is off in dev.
                .connectedTo(uris)
                .build();
    }

    /**
     * @param uris the configured ES endpoint (host:port)
     */
    public void setUris(String uris) {
        this.uris = uris;
    }

    /**
     * @return the configured ES endpoint
     */
    public String getUris() {
        return uris;
    }
}
