package com.smartshop.order.event;

import com.smartshop.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OrderCancelledEvent - Emitted when order cannot be completed.
 *
 * <h2>Purpose</h2>
 * Allows notification and downstream compensating workflows to react to cancellation.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Compensation signaling: represents rollback path in distributed saga.</li>
 *   <li>Failure transparency: includes reason for operational diagnosis.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Published by saga orchestrator when a distributed step fails.
 *
 * @see com.smartshop.order.service.SagaOrchestrator
 * @author SmartShop Team
 */
@Getter
@NoArgsConstructor
public class OrderCancelledEvent extends BaseEvent {

    private Long orderId;
    private String reason;

    /**
     * Constructs cancelled event payload.
     *
     * @param orderId cancelled order id
     * @param reason cancellation reason
     */
    public OrderCancelledEvent(final Long orderId, final String reason) {
        super("ORDER_CANCELLED");
        this.orderId = orderId;
        this.reason = reason;
    }
}
