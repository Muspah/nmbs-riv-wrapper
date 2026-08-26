package nl.ovdata.nmbs.internal;

import java.security.SecureRandom;

/**
 * Generates the short, opaque, per-request/per-device identifiers ({@code x-correlation-id},
 * {@code x-device-request-id}) that the official BelgianTrain mobile app sends with every request.
 *
 * <p>The backend does not appear to validate these beyond "present and non-empty", but requests observed in the
 * wild always carry a fresh 21-character alphanumeric id, so this class reproduces that shape.</p>
 */
public final class IdGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 21;
    private static final SecureRandom RANDOM = new SecureRandom();

    private IdGenerator() {
    }

    public static String next() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
