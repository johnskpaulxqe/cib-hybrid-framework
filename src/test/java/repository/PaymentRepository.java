package repository;

import java.util.List;
import java.util.Map;

/**
 * Example repository for the cib_payments table. Copy this pattern for any
 * other entity (TradeFinanceRepository, FxDealRepository, AccountRepository...).
 */
public class PaymentRepository extends BaseRepository {

    private static final String TABLE = "cib_payments";

    /** Existence check — e.g. confirm a payment created via UI/API actually landed in the DB. */
    public boolean paymentExistsById(String paymentId) {
        return existsByField(TABLE, "payment_id", paymentId);
    }

    /** Fetch the full row for a payment. */
    public Map<String, Object> getPaymentById(String paymentId) {
        return findOneByField(TABLE, "payment_id", paymentId);
    }

    /** Column getter — e.g. verify a payment moved from PENDING to SUBMITTED after approval. */
    public String getPaymentStatus(String paymentId) {
        return getColumnAsString(getPaymentById(paymentId), "status");
    }

    public String getPaymentAmount(String paymentId) {
        return getColumnAsString(getPaymentById(paymentId), "amount");
    }

    /** All payments for a given corporate entity — e.g. verifying an account summary list against the DB. */
    public List<Map<String, Object>> getPaymentsByEntityId(String entityId) {
        return findAllByField(TABLE, "entity_id", entityId);
    }

    public int countPendingPaymentsForEntity(String entityId) {
        return findByCustomQuery(
                "SELECT * FROM " + TABLE + " WHERE entity_id = ? AND status = 'PENDING'", entityId).size();
    }

    /** Cleanup — used in @After hooks so test-created payments don't accumulate across runs. */
    public int deleteTestPayment(String paymentId) {
        return deleteByField(TABLE, "payment_id", paymentId);
    }
}
