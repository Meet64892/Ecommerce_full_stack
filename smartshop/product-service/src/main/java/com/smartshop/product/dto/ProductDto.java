package com.smartshop.product.dto;

/**
 * ProductDto - Product response projection.
 *
 * <h2>Purpose</h2>
 * Decouples external API representation from JPA internals and lazy-loading behavior.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>DTO encapsulation: prevents entity leakage over API boundaries.</li>
 *   <li>Stable contract: DTO shape survives persistence refactors.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Returned by product controllers for CRUD and search results.
 *
 * @see ProductCreateRequest
 * @author SmartShop Team
 */
public record ProductDto(Long id, String name, String description, Double price, Integer rating, Long categoryId, String categoryName) {
}
