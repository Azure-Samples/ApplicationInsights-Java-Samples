package com.example.models;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * Data struct to contain only C section with custom fields.
 */
public final class MonitorBase implements JsonSerializable<MonitorBase> {
    /*
     * Name of item (B section) if any. If telemetry data is derived straight from this, this should be null.
     */
    private String baseType;

    /*
     * The data payload for the telemetry request
     */
    private MonitorDomain baseData;

    /**
     * Creates an instance of MonitorBase class.
     */
    public MonitorBase() {
    }

    /**
     * Get the baseType property: Name of item (B section) if any. If telemetry data is derived straight from this, this
     * should be null.
     *
     * @return the baseType value.
     */
    public String getBaseType() {
        return this.baseType;
    }

    /**
     * Set the baseType property: Name of item (B section) if any. If telemetry data is derived straight from this, this
     * should be null.
     *
     * @param baseType the baseType value to set.
     * @return the MonitorBase object itself.
     */
    public MonitorBase setBaseType(String baseType) {
        this.baseType = baseType;
        return this;
    }

    /**
     * Get the baseData property: The data payload for the telemetry request.
     *
     * @return the baseData value.
     */
    public MonitorDomain getBaseData() {
        return this.baseData;
    }

    /**
     * Set the baseData property: The data payload for the telemetry request.
     *
     * @param baseData the baseData value to set.
     * @return the MonitorBase object itself.
     */
    public MonitorBase setBaseData(MonitorDomain baseData) {
        this.baseData = baseData;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("baseType", this.baseType);
        jsonWriter.writeJsonField("baseData", this.baseData);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of MonitorBase from the JsonReader.
     *
     * @param jsonReader The JsonReader being read.
     * @return An instance of MonitorBase if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IOException If an error occurs while reading the MonitorBase.
     */
    public static MonitorBase fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            MonitorBase deserializedMonitorBase = new MonitorBase();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("baseType".equals(fieldName)) {
                    deserializedMonitorBase.baseType = reader.getString();
                } else if ("baseData".equals(fieldName)) {
                    deserializedMonitorBase.baseData = MonitorDomain.fromJson(reader);
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedMonitorBase;
        });
    }
}
