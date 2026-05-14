package util;

import java.util.UUID;

/**
 * Generates unique IDs for various entities.
 */
public class IDGenerator {

    public static String generateUniqueId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static String generatePassId() {
        return "PASS-" + generateUniqueId();
    }

    public static String generatePaymentRef() {
        return "PAY-" + generateUniqueId();
    }

    public static String generateCertificateId() {
        return "CERT-" + generateUniqueId();
    }
}
