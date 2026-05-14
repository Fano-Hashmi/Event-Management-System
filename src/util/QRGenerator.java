package util;

import java.util.UUID;

/**
 * QR Code generator and verifier.
 * Generates unique QR code strings for event passes.
 * Uses encoded format: QR-{PASS_ID}-{USER_ID}-{EVENT_ID}-{HASH}
 */
public class QRGenerator {

    /**
     * Generate a unique QR code string for a pass.
     */
    public static String generateQRCode(int passId, int userId, int eventId) {
        String raw = passId + "-" + userId + "-" + eventId + "-" + System.currentTimeMillis();
        String hash = Validation.hashPassword(raw).substring(0, 12).toUpperCase();
        return "QR-" + passId + "-" + userId + "-" + eventId + "-" + hash;
    }

    /**
     * Generate a QR code for a new pass (before passId is known).
     */
    public static String generateQRCode(int userId, int eventId) {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "QR-" + userId + "-" + eventId + "-" + uuid;
    }

    /**
     * Verify a QR code format is valid.
     */
    public static boolean isValidQRCode(String qrCode) {
        if (qrCode == null) return false;
        String normalized = qrCode.trim().toUpperCase();
        return normalized.startsWith("QR-") && normalized.split("-").length >= 4;
    }

    /**
     * Extract user ID from QR code.
     */
    public static int extractUserId(String qrCode) {
        try {
            String[] parts = qrCode.split("-");
            if (parts.length >= 3) return Integer.parseInt(parts[2]);
        } catch (NumberFormatException ignored) {}
        return -1;
    }

    /**
     * Extract event ID from QR code.
     */
    public static int extractEventId(String qrCode) {
        try {
            String[] parts = qrCode.split("-");
            if (parts.length >= 4) return Integer.parseInt(parts[3]);
        } catch (NumberFormatException ignored) {}
        return -1;
    }

    /**
     * Generate a visual text-based QR pattern for display.
     */
    public static String[] generateQRPattern(String qrCode) {
        int hash = Math.abs(qrCode.hashCode());
        int size = 11;
        String[] lines = new String[size];
        
        for (int y = 0; y < size; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < size; x++) {
                // Fixed finder patterns in corners
                if (isFinderPattern(x, y, size)) {
                    sb.append("\u2588\u2588");
                } else {
                    // Data pattern based on hash
                    int bit = ((hash >> ((x * size + y) % 31)) & 1);
                    sb.append(bit == 1 ? "\u2588\u2588" : "  ");
                }
            }
            lines[y] = sb.toString();
        }
        return lines;
    }

    private static boolean isFinderPattern(int x, int y, int size) {
        // Top-left corner finder
        if (x <= 2 && y <= 2) return (x == 0 || x == 2 || y == 0 || y == 2 || (x == 1 && y == 1));
        // Top-right corner finder
        if (x >= size - 3 && y <= 2) return ((x == size-3 || x == size-1) || y == 0 || y == 2 || (x == size-2 && y == 1));
        // Bottom-left corner finder
        if (x <= 2 && y >= size - 3) return (x == 0 || x == 2 || (y == size-3 || y == size-1) || (x == 1 && y == size-2));
        return false;
    }
}
