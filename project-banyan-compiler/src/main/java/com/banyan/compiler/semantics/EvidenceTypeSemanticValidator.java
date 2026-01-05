package com.banyan.compiler.semantics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EvidenceTypeSemanticValidator implements SemanticValidator {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "BOOLEAN",
            "INTEGER",
            "DECIMAL",
            "STRING",
            "DURATION"
    );

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<String> validate(String dslJson) {
        try {
            JsonNode json = mapper.readTree(dslJson);
            List<String> semanticViolations = new ArrayList<>();
            Set<String> uniqueNames = new HashSet<>();

            /*
               Rule 1: Unique Name
               Rule 2: Allowed field types:
               Rule 3 : A minimum of one required field
             */
            // Get the fields
            JsonNode fields = json.at("/spec/fields");
            Boolean atLeastOneFieldIsRequired = false;
            // Rule 1:
            for(JsonNode field: fields) {

                if(! uniqueNames.add(field.at("/name").asText(null)) )
                {
                    semanticViolations.add("EVIDENCE_TYPE_ERR 10001: Duplicate Names for fields are'nt supported. The inidvidual field names should be unique");
                }
                if(!ALLOWED_TYPES.contains(field.at("/type").asText(null)))
                {
                    semanticViolations.add("EVIDENCE_TYPE_ERR 10002: Not a valid 'type'.Allowed values are anyone of "+ String.join("", ALLOWED_TYPES));
                }
                Boolean required = field.at("/required").asBoolean();
                if(required) {
                    // Rule 3
                    atLeastOneFieldIsRequired = true;
                }
            }
            if(!atLeastOneFieldIsRequired) {
                semanticViolations.add("EVIDENCE_TYPE_ERR 10003: Not one field is required.");
            }

            return semanticViolations;
        }catch(Exception e)
        {
            return List.of(e.getMessage());
        }
    }
}
