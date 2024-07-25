package com.example.models;

import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;

import java.io.IOException;

public final class ResponseError implements JsonSerializable<ResponseError> {
    private int index;
    private int statusCode;
    private String message;

    public ResponseError setIndex(int index) {
        this.index = index;
        return this;
    }

    public ResponseError setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseError setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "ErrorResponse{" +
            "index=" + index +
            ", statusCode=" + statusCode +
            ", message='" + message + '}';
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        return jsonWriter.writeStartObject()
            .writeIntField("index", index)
            .writeIntField("statusCode", statusCode)
            .writeStringField("message", message)
            .writeEndObject();
    }

    public static ResponseError fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            ResponseError deserializedValue = new ResponseError();

            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                // In this case field names are case-sensitive but this could be replaced with 'equalsIgnoreCase' to
                // make them case-insensitive.
                if ("index".equals(fieldName)) {
                    deserializedValue.setIndex(reader.getInt());
                } else if ("statusCode".equals(fieldName)) {
                    deserializedValue.setStatusCode(reader.getInt());
                } else if ("message".equals(fieldName)) {
                    deserializedValue.setMessage(reader.getString());
                } else {
                    // Fallthrough case of an unknown property. In this instance the value is skipped, if it's a JSON
                    // array or object the reader will progress until it terminated. This could also throw an exception
                    // if unknown properties should cause that or be read into an additional properties Map for further
                    // usage.
                    reader.skipChildren();
                }
            }

            return deserializedValue;
        });
    }
}
