// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;

@RestController
public class ExampleController {

    @GetMapping("/")
    public String root() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            receiveOneRequestAsync(serverSocket);
            sendOneRequest(serverSocket.getLocalPort());
        }
        return "OK";
    }

    @WithSpan(kind = SpanKind.CLIENT)
    private static void sendOneRequest(int port) throws IOException {
        try (Socket localSocket = new Socket("localhost", port);
             DataOutputStream out = new DataOutputStream(localSocket.getOutputStream())) {

            out.writeUTF(Span.current().getSpanContext().getTraceId());
            out.writeUTF(Span.current().getSpanContext().getSpanId());
        }
    }

    private static void receiveOneRequestAsync(ServerSocket serverSocket) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                receiveOneRequest(serverSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void receiveOneRequest(ServerSocket serverSocket) throws IOException {
        try (Socket socket = serverSocket.accept();
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            String traceId = in.readUTF();
            String parentSpanId = in.readUTF();

            // "parent span" represents the CLIENT span that was propagated over the wire
            SpanContext parentSpanContext = SpanContext.create(traceId, parentSpanId, TraceFlags.getSampled(), TraceState.getDefault());
            Span parentSpan = Span.wrap(parentSpanContext);

            try (Scope ignored = parentSpan.makeCurrent()) {
                receiveOneRequest();
            }
        }
    }

    @WithSpan(kind = SpanKind.SERVER)
    private static void receiveOneRequest() throws IOException {
        // everything inside this method will be parented to the SERVER span created by @WithSpan
        somethingThatIsAutoInstrumented();
    }

    private static void somethingThatIsAutoInstrumented() throws IOException {
        URL obj = new URL("https://mock.codes/200");
        URLConnection connection = obj.openConnection();
        try (InputStream content = connection.getInputStream()) {
            // drain the content
            byte[] buffer = new byte[1024];
            while (content.read(buffer) != -1) {
            }
        }
    }
}
