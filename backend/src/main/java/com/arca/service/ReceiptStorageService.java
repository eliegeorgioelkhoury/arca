package com.arca.service;

/**
 * Abstraction over receipt file storage. A local-filesystem implementation backs
 * dev; a Supabase Storage implementation is swapped in at deploy (milestone 6).
 */
public interface ReceiptStorageService {

    StoredReceipt store(byte[] content, String originalFilename, String contentType);

    record StoredReceipt(String storageKey, long sizeBytes) {
    }
}
