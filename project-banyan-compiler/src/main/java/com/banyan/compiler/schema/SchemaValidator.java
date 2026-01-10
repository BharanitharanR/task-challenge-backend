package com.banyan.compiler.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface SchemaValidator {
    List<String> validate(String json);
}

