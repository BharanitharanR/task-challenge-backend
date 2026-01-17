package com.banyan.compiler.backend.spi;

import com.banyan.compiler.backend.api.CompilationMetadata;
import com.banyan.compiler.backend.api.CompiledArtifact;
import com.fasterxml.jackson.databind.JsonNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public abstract class AbstractBackendCompiler<T extends CompiledArtifact<?>>
        implements BackendCompiler<T> {

    protected String readId(JsonNode root) {
        return root.at("/id").asText();
    }

    protected int readVersion(JsonNode root) {
        return root.at("/version").asInt();
    }


    protected CompilationMetadata metadata(JsonNode root) {
        return new CompilationMetadata(
                compilerVersion(),
                System.currentTimeMillis(),
                hash(root.toString())
        );
    }

    protected String compilerVersion() {
        return "banyan-compiler-2.0";
    }

    private String hash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("HASH_GENERATION_FAILED", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
