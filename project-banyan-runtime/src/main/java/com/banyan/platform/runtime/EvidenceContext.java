package com.banyan.platform.runtime;
// For phase 1 a strict decisoin to avoid Lomboks. Results over anything
// import lombok.Getter;
// import lombok.Setter;

import java.util.Map;

public final class EvidenceContext {

    private final Map<String, Object> values;

    public EvidenceContext(Map<String, Object> values) {
        this.values = Map.copyOf(values);
    }

    public Object get(String key) {
        return values.get(key);
    }
}


