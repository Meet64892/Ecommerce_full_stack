package com.smartshop.product.config;

import com.smartshop.product.entity.Product;
import com.smartshop.product.repository.jpa.ProductRepository;
import com.smartshop.product.repository.search.ProductSearchRepository;
import com.smartshop.product.service.ProductIndexingSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ensures the products index exists and syncs PostgreSQL catalog rows into Elasticsearch on startup.
 */
@Component
@ConditionalOnProperty(name = "smartshop.elasticsearch.reindex-on-startup", havingValue = "true", matchIfMissing = true)
public class ElasticsearchIndexInitializer {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchIndexInitializer.class);

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticsearchIndexInitializer(ProductRepository productRepository,
                                         ProductSearchRepository productSearchRepository,
                                         ElasticsearchOperations elasticsearchOperations) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void syncIndexOnStartup() {
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(Product.class);
            if (!indexOps.exists()) {
                indexOps.create();
            }
            indexOps.putMapping();

            long indexed = productSearchRepository.count();
            if (indexed > 0) {
                log.info("Elasticsearch products index already contains {} documents", indexed);
                return;
            }

            int page = 0;
            int total = 0;
            Page<Product> batch;
            do {
                batch = productRepository.findAll(PageRequest.of(page, 100));
                batch.forEach(product -> {
                    ProductIndexingSupport.prepareForIndexing(product);
                    productSearchRepository.save(product);
                });
                total += batch.getNumberOfElements();
                page++;
            } while (batch.hasNext());

            log.info("Elasticsearch reindex complete: {} products indexed", total);
        } catch (Exception ex) {
            log.warn("Elasticsearch index sync skipped: {}", ex.getMessage());
        }
    }
}
