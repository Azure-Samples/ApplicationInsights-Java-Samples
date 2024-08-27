package com.example;

import com.azure.json.JsonProviders;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.example.models.AppInsightsByteBufferPool;
import com.example.models.ByteBufferOutputStream;
import com.example.models.Response;
import com.example.models.TelemetryItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Main {

    private static final String EXPECTED_RESPONSE_JSON;
    private static final byte[] EXPECTED_TELEMETRY_ITEMS_BYTES;

    static {
        try {
            EXPECTED_RESPONSE_JSON = Resources.readString("206_response_body.json");
            EXPECTED_TELEMETRY_ITEMS_BYTES = Resources.readBytes("request_body.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final AppInsightsByteBufferPool byteBufferPool = new AppInsightsByteBufferPool();

    public static void main(String[] args) throws IOException {
//        testResponseWithErrors();
        testTelemetryItems();
//        testAddNewLineDelimiter();
    }

    private static void testAddNewLineDelimiter() throws IOException {
        // deserialize expected telemetry items
//        List<TelemetryItem> expectedTelemetryItems = deserialize(EXPECTED_TELEMETRY_ITEMS_BYTES);
//        System.out.println("expectedTelemetryItems size: " + expectedTelemetryItems.size());
//
//        List<ByteBuffer> result = addNewLineAsLineDelimiter(serialize(expectedTelemetryItems));
//        System.out.println("resultByteArrayList size: " + result.size());
//        System.out.println(result.size() == byteArrayList.size());
//
//        List<byte[]> byteArrayList2 = splitBytesByNewline(convertByteBufferListToByteArray(result));
//        List<TelemetryItem> actualTelemetryItems = new ArrayList<>();
//        for (byte[] bytes2 : byteArrayList2) {
//            actualTelemetryItems.add(deserialize(bytes2));
//        }
//        System.out.println("actualTelemetryItems size: " + actualTelemetryItems.size());
//        System.out.println("==============================================");
//        System.out.println(printJson(expectedTelemetryItems).equals(printJson(actualTelemetryItems)));
//        System.out.println("==============================================");
    }

    public static List<ByteBuffer> addNewLineAsLineDelimiter(List<ByteBuffer> byteBuffers) {
        List<ByteBuffer> result = new ArrayList<>();
        for (int i = 0; i < byteBuffers.size(); i++) {
            ByteBuffer byteBuffer = byteBuffers.get(i);
            ByteBuffer newByteBuffer;
            if (i < byteBuffers.size() - 1) {
                ByteBuffer newLine = ByteBuffer.wrap(new byte[]{'\n'});
                newByteBuffer = ByteBuffer.allocate(byteBuffer.remaining() + newLine.remaining());
                newByteBuffer.put(byteBuffer);
                newByteBuffer.put(newLine);
                newByteBuffer.flip();
            } else {
                newByteBuffer = ByteBuffer.allocate(byteBuffer.remaining());
                newByteBuffer.put(byteBuffer);
                newByteBuffer.flip();
            }
            result.add(newByteBuffer);
        }
        return result;
    }

    private static void testTelemetryItems() throws IOException {
        // load telemetry items from file
        List<byte[]> byteArrayList = splitBytesByNewline(EXPECTED_TELEMETRY_ITEMS_BYTES);
        System.out.println("byteArrayList size: " + byteArrayList.size());
        List<TelemetryItem> telemetryItems = deserializeWithNewLines(EXPECTED_TELEMETRY_ITEMS_BYTES);
        System.out.println("telemetryItems size: " + telemetryItems.size());

        System.out.println("====================================");
        System.out.println(printJson(telemetryItems));
        System.out.println("====================================");

        // serialize telemetry items with encoding and add newline delimiter
        List<ByteBuffer> serializedByteBuffer = serialize(telemetryItems);
        List<TelemetryItem> deserializedTelemetryItems = deserializeWithNewLines(decode(convertByteBufferListToByteArray(serializedByteBuffer)));

        assert deserializedTelemetryItems != null;
        System.out.println(deserializedTelemetryItems.size() == telemetryItems.size());
        System.out.println("====================================");
        System.out.println(printJson(deserializedTelemetryItems).equals(printJson(telemetryItems)));
    }

    private static String printJson(List<TelemetryItem> telemetryItems) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (TelemetryItem telemetryItem : telemetryItems) {
            sb.append(serialize(telemetryItem));
            if (index < telemetryItems.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static void testResponseWithErrors() throws IOException {
        try (JsonReader reader = JsonProviders.createReader(EXPECTED_RESPONSE_JSON)) {
            Response response = Response.fromJson(reader);
            String actualJson = serialize(response);
            System.out.println("**** start testResponseWithErrors ****");
            System.out.println("Actual json matches the expected json: " + EXPECTED_RESPONSE_JSON.equals(actualJson));
            System.out.println("**** end testResponseWithErrors ****");
        }
    }

    private static String serialize(Object obj) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             JsonWriter writer = JsonProviders.createWriter(outputStream)) {
            if (obj instanceof Response) {
                Response response = (Response) obj;
                response.toJson(writer);
            } else if (obj instanceof TelemetryItem) {
                TelemetryItem telemetryItem = (TelemetryItem) obj;
                telemetryItem.toJson(writer);
            } else {
                throw new IllegalArgumentException("Unknown object type: " + obj.getClass());
            }
            writer.flush();
            return outputStream.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ByteArrayOutputStream writeTelemetryItemsAsByteArrayOutputStream(List<TelemetryItem> telemetryItems) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int i = 0; i < telemetryItems.size(); i++) {
            JsonWriter jsonWriter = JsonProviders.createWriter(result);
            telemetryItems.get(i).toJson(jsonWriter);
            jsonWriter.flush();

            if (i < telemetryItems.size() - 1) {
                System.out.println("write newline i - " + i);
                result.write('\n');
            }
        }
        return result;
    }

    private static ByteBufferOutputStream writeTelemetryItemsAsByteBufferOutputStream(List<TelemetryItem> telemetryItems) throws IOException {
        try (ByteBufferOutputStream result = new ByteBufferOutputStream(byteBufferPool)) {
            JsonWriter jsonWriter = null;
            for (int i = 0; i < telemetryItems.size(); i++) {
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(result);
                jsonWriter = JsonProviders.createWriter(gzipOutputStream);
                telemetryItems.get(i).toJson(jsonWriter);
                jsonWriter.flush();
                gzipOutputStream.finish();

                if (i < telemetryItems.size() - 1) {
                    result.write('\n');
                }
            }
            if (jsonWriter != null) {
                jsonWriter.close();
            }
            return result;
        }
    }

    // deserialize byte array to list of TelemetryItems
    public static List<TelemetryItem> deserialize(byte[] rawBytes) {
        byte[] decodedRawBytes = decode(rawBytes);
        try (JsonReader jsonReader = JsonProviders.createReader(rawBytes)) {
            JsonToken token = jsonReader.currentToken();
            if (token == null) { // Only time null is returned by currentToken is when the parser hasn't been initialized
                token = jsonReader.nextToken();
            }
            if (token == JsonToken.START_ARRAY) {
                return jsonReader.readArray(TelemetryItem::fromJson);
            } else if (token == JsonToken.START_OBJECT) {
                return Collections.singletonList(TelemetryItem.fromJson(jsonReader));
            } else if (token == JsonToken.NULL) {
                return null;
            } else {
                throw new IllegalArgumentException("Failed to deserialize byte[] to list of TelemetryItems");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<TelemetryItem> deserializeWithNewLines(byte[] rawBytes) {
        try (JsonReader jsonReader = JsonProviders.createReader(rawBytes)) {
            JsonToken token = jsonReader.currentToken();
            if (token == null) {
                token = jsonReader.nextToken();
            }

            List<TelemetryItem> result = new ArrayList<>();
            do {
                result.add(TelemetryItem.fromJson(jsonReader));
            } while (jsonReader.nextToken() == JsonToken.START_OBJECT);
            System.out.println(result.size());
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // decode gzipped TelemetryItems raw bytes back to original TelemetryItems raw bytes
    private static byte[] decode(byte[] rawBytes) {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(rawBytes));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int read;
            while ((read = in.read(data)) != -1) {
                baos.write(data, 0, read);
            }
            return baos.toByteArray();
        } catch (EOFException e) {
            throw new IllegalStateException("Unexpected end of ZLIB input stream, the input data may be corrupted or incomplete", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to decode byte[]", e);
        }
    }

    public static List<ByteBuffer> serialize(List<TelemetryItem> telemetryItems) {
        try {
            ByteBufferOutputStream out = writeTelemetryItemsAsByteBufferOutputStream(telemetryItems);
            out.close(); // closing ByteBufferOutputStream is a no-op, but this line makes LGTM happy
            List<ByteBuffer> byteBuffers = out.getByteBuffers();
            for (ByteBuffer byteBuffer : byteBuffers) {
                byteBuffer.flip();
            }
            return out.getByteBuffers();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode list of TelemetryItems to byte[]", e);
        }
    }

    private static int countNewLines(byte[] input) {
        int count = 0;
        for (byte b : input) {
            if (b == '\n') {
                count++;
            }
        }
        return count;
    }

    public static List<byte[]> splitBytesByNewline(byte[] inputBytes) {
        List<byte[]> lines = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < inputBytes.length; i++) {
            if (inputBytes[i] == '\n') {
                byte[] line = new byte[i - start];
                System.arraycopy(inputBytes, start, line, 0, i - start);
                lines.add(line);
                start = i + 1;
            }
        }
        // Add the last line (if any)
        if (start < inputBytes.length) {
            byte[] lastLine = new byte[inputBytes.length - start];
            System.arraycopy(inputBytes, start, lastLine, 0, inputBytes.length - start);
            lines.add(lastLine);
        }
        return lines;
    }

    static byte[] convertByteBufferListToByteArray(List<ByteBuffer> byteBuffers) {
        int totalSize = byteBuffers.stream().mapToInt(ByteBuffer::remaining).sum();
        ByteBuffer resultBuffer = ByteBuffer.allocate(totalSize);

        for (ByteBuffer buffer : byteBuffers) {
            byte[] byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);
            resultBuffer.put(byteArray);
        }

        return resultBuffer.array();
    }
}
