package com.smartshop.product.service;

import com.smartshop.product.entity.Product;

/**
 * Prepares JPA products for Elasticsearch indexing by flattening category fields.
 */
public final class ProductIndexingSupport {

    private ProductIndexingSupport() {
    }

    /**
     * Copies category id and name onto transient search fields before indexing.
     *
     * @param product product loaded with its category association
     */
    public static void prepareForIndexing(Product product) {
        if (product.getCategory() != null) {
            product.setCategoryId(product.getCategory().getId());
            product.setCategoryName(product.getCategory().getName());
        }
    }
}
