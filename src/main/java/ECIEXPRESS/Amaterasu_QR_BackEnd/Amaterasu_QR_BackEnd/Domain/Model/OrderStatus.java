package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Domain.Model;

/**
 * Enumeration defining the valid business states of an order.
 * Used within the domain to ensure only controlled, meaningful, and
 * traceable order statuses are assigned to QR records.
 */
public enum OrderStatus {
    CANCELLED,
    DELIVERED,
    PAYED,
    REFUNDED
}
