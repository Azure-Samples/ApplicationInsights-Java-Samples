package com.example;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        RequestProcessor requestProcessor = new RequestProcessor(serverSocket);
        requestProcessor.setDaemon(true);
        requestProcessor.start();

        sendOneRequest(serverSocket.getLocalPort());

        // give time for the request to be handled before exiting
        Thread.sleep(1000);
    }

    @WithSpan(kind = SpanKind.CLIENT) // this annotation will generate dependency telemetry
    private static void sendOneRequest(int port) throws IOException {
        try (Socket localSocket = new Socket("localhost", port);
             DataOutputStream out = new DataOutputStream(localSocket.getOutputStream())) {

            out.writeUTF(Span.current().getSpanContext().getTraceId());
            out.writeUTF(Span.current().getSpanContext().getSpanId());
        }
    }

    private static class RequestProcessor extends Thread {

        private final ServerSocket serverSocket;

        private RequestProcessor(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    try (Socket socket = serverSocket.accept()) {
                        process(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private static void process(Socket socket) throws IOException {
            try (DataInputStream in = new DataInputStream(socket.getInputStream())) {

                String traceId = in.readUTF();
                String parentSpanId = in.readUTF();

                // "parent span" represents the CLIENT span that was propagated over the wire
                SpanContext parentSpanContext = SpanContext.create(traceId, parentSpanId, TraceFlags.getSampled(), TraceState.getDefault());
                Span parentSpan = Span.wrap(parentSpanContext);

                // making the parent span context current will cause the request telemetry below
                // to be parented to it
                try (Scope ignored = parentSpan.makeCurrent()) {
                    handleRequest();
                }
            }
        }

        // this annotation will generate request telemetry and make it current
        @WithSpan(kind = SpanKind.SERVER)
        private static void handleRequest() throws IOException {
            // everything inside this method will be parented to the request telemetry
            doSomethingThatIsAutoInstrumented();
        }

        private static void doSomethingThatIsAutoInstrumented() throws IOException {
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
}
