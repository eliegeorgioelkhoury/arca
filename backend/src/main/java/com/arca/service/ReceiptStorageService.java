package com.arca.service;

/**
 * Abstraction over receipt file storage. A local-filesystem implementation backs
 * dev/test; a Supabase Storage implementation is used in prod (milestone 6).
 */
public interface ReceiptStorageService {

    StoredReceipt store(byte[] content, String originalFilename, String contentType);

    /**
     * A short-lived signed URL for reading a stored receipt, or {@code null} when
     * the backend does not serve signed URLs (dev/local).
     */
    String signedUrl(String storageKey);

    record StoredReceipt(String storageKey, long sizeBytes) {
    }
}
