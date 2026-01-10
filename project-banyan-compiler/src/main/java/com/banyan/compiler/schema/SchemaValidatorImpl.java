package com.banyan.compiler.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.smallrye.common.constraint.NotNull;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class SchemaValidatorImpl implements SchemaValidator{
    private static final ObjectMapper mapper = new ObjectMapper();
    private final JsonSchema schema;
    private final String schemaType ;

    public SchemaValidatorImpl(@NotNull  String resourceName, @NotNull String exceptionErr, @NotNull String schemaType) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        Optional<InputStream> schemaStream = Optional.ofNullable(getClass().getResourceAsStream(resourceName));
        this.schemaType = schemaType;
        if(schemaStream.isPresent())
            this.schema = factory.getSchema(schemaStream.get());
        else
            throw new IllegalStateException(
                    exceptionErr);
    }

    @Override
    public List<String> validate(String json) {
        try
        {
            JsonNode dslJson = mapper.readTree(json);
            return this.schema.validate(dslJson).stream().map(ValidationMessage::getMessage).toList();
        }
        catch(Exception e)
        {
            return List.of( (this.schemaType).toUpperCase()+"_PARSE_ERROR: " +
                    (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }

    }
}
