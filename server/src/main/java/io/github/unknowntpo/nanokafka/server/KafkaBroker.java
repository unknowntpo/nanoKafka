package io.github.unknowntpo.nanokafka.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Main entry point for the NanoKafka broker server.
 * Starts a gRPC server listening on port 50051.
 */
public class KafkaBroker {
    private static final Logger logger = Logger.getLogger(KafkaBroker.class.getName());
    private static final int PORT = 50051;

    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
            .addService(new KafkaBrokerServiceImpl())
            .build()
            .start();

        logger.info("NanoKafka Broker started on port " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server due to JVM shutdown");
            try {
                KafkaBroker.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("Server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final KafkaBroker broker = new KafkaBroker();
        broker.start();
        broker.blockUntilShutdown();
    }
}
