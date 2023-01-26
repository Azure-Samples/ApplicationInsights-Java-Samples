package com.example;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SpringBootApplication
public class ManualTracePropagation {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        RequestProcessor requestProcessor = new RequestProcessor(serverSocket);
        requestProcessor.start();

        sendOneRequest(serverSocket.getLocalPort());

        // give time for the request to be handled before exiting
        Thread.sleep(1000);

        System.exit(0);
    }

    @WithSpan(kind = SpanKind.CLIENT) // this annotation will generate dependency telemetry
    private static void sendOneRequest(int port) throws IOException {
        try (Socket localSocket = new Socket("localhost", port);
             DataOutputStream out = new DataOutputStream(localSocket.getOutputStream())) {

            out.writeUTF(Span.current().getSpanContext().getTraceId());
            out.writeUTF(Span.current().getSpanContext().getSpanId());
        }
    }
}
