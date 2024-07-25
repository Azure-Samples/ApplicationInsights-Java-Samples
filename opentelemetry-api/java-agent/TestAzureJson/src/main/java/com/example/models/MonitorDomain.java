package com.example.models;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The abstract common base of all domains.
 */
public class MonitorDomain implements JsonSerializable<MonitorDomain> {
    /*
     * Schema version
     */
    private int version;

    /*
     * The abstract common base of all domains.
     */
    private Map<String, Object> additionalProperties;

    /**
     * Creates an instance of MonitorDomain class.
     */
    public MonitorDomain() {
    }

    /**
     * Get the version property: Schema version.
     *
     * @return the version value.
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * Set the version property: Schema version.
     *
     * @param version the version value to set.
     * @return the MonitorDomain object itself.
     */
    public MonitorDomain setVersion(int version) {
        this.version = version;
        return this;
    }

    /**
     * Get the additionalProperties property: The abstract common base of all domains.
     *
     * @return the additionalProperties value.
     */
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     * Set the additionalProperties property: The abstract common base of all domains.
     *
     * @param additionalProperties the additionalProperties value to set.
     * @return the MonitorDomain object itself.
     */
    public MonitorDomain setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeIntField("ver", this.version);
        if (additionalProperties != null) {
            for (Map.Entry<String, Object> additionalProperty : additionalProperties.entrySet()) {
                jsonWriter.writeUntypedField(additionalProperty.getKey(), additionalProperty.getValue());
            }
        }
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of MonitorDomain from the JsonReader.
     *
     * @param jsonReader The JsonReader being read.
     * @return An instance of MonitorDomain if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the MonitorDomain.
     */
    public static MonitorDomain fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            MonitorDomain deserializedMonitorDomain = new MonitorDomain();
            Map<String, Object> additionalProperties = null;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("ver".equals(fieldName)) {
                    deserializedMonitorDomain.version = reader.getInt();
                } else {
                    if (additionalProperties == null) {
                        additionalProperties = new LinkedHashMap<>();
                    }

                    additionalProperties.put(fieldName, reader.readUntyped());
                }
            }
            deserializedMonitorDomain.additionalProperties = additionalProperties;

            return deserializedMonitorDomain;
        });
    }
}
