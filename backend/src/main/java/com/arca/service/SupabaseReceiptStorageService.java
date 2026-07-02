package com.arca.service;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Production receipt storage on Supabase Storage. Uploads bytes to a private
 * bucket using the service key, and returns short-lived signed URLs for reads.
 * Configured entirely from env (see application-prod.yml): no secrets in code.
 */
@Service
@Profile("prod")
public class SupabaseReceiptStorageService implements ReceiptStorageService {

    private final RestClient client;
    private final String storageBaseUrl;
    private final String bucket;
    private final long signedUrlExpirySeconds;

    public SupabaseReceiptStorageService(
            RestClient.Builder builder,
            @Value("${app.supabase.url}") String supabaseUrl,
            @Value("${app.supabase.service-key}") String serviceKey,
            @Value("${app.supabase.bucket:receipts}") String bucket,
            @Value("${app.supabase.signed-url-expiry-seconds:3600}") long signedUrlExpirySeconds) {
        this.storageBaseUrl = supabaseUrl + "/storage/v1";
        this.bucket = bucket;
        this.signedUrlExpirySeconds = signedUrlExpirySeconds;
        this.client = builder
                .baseUrl(this.storageBaseUrl)
                // New Supabase secret keys (sb_secret_...) are not JWTs and authenticate
                // via the apikey header. Send the key there, and mirror it in Authorization
                // (Bearer = same key) so both new secret keys and legacy service_role JWTs work.
                .defaultHeader("apikey", serviceKey)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .build();
    }

    @Override
    public StoredReceipt store(byte[] content, String originalFilename, String contentType) {
        String key = UUID.randomUUID() + extension(originalFilename);
        client.post()
                .uri("/object/{bucket}/{key}", bucket, key)
                .contentType(contentType != null
                        ? MediaType.parseMediaType(contentType)
                        : MediaType.APPLICATION_OCTET_STREAM)
                .header("x-upsert", "true")
                .body(content)
                .retrieve()
                .toBodilessEntity();
        return new StoredReceipt(key, content.length);
    }

    @Override
    public String signedUrl(String storageKey) {
        SignedUrlResponse response = client.post()
                .uri("/object/sign/{bucket}/{key}", bucket, storageKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("expiresIn", signedUrlExpirySeconds))
                .retrieve()
                .body(SignedUrlResponse.class);
        return response != null ? storageBaseUrl + response.signedURL() : null;
    }

    private static String extension(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }

    /** Supabase returns {@code {"signedURL": "/object/sign/<bucket>/<key>?token=..."}}. */
    private record SignedUrlResponse(String signedURL) {
    }
}
