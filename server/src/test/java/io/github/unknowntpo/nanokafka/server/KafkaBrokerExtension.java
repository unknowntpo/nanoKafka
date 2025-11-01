package io.github.unknowntpo.nanokafka.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * JUnit 5 extension that starts/stops KafkaBroker server for integration tests.
 * Reads port from @WithKafkaBroker annotation (defaults to 50051).
 */
public class KafkaBrokerExtension implements BeforeAllCallback, AfterAllCallback {
    private static final Logger logger = Logger.getLogger(KafkaBrokerExtension.class.getName());
    private static final String NAMESPACE = "kafkaBroker";
    private static final String SERVER_KEY = "server";
    private static final String PORT_KEY = "port";

    private Server server;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // Get port from annotation
        WithKafkaBroker annotation = context.getRequiredTestClass().getAnnotation(WithKafkaBroker.class);
        int port = annotation.port();

        logger.info("Starting KafkaBroker server on port " + port);

        server = ServerBuilder.forPort(port)
            .addService(new KafkaBrokerServiceImpl())
            .build()
            .start();

        // Store port in context for test classes to access
        context.getStore(ExtensionContext.Namespace.create(NAMESPACE))
            .put(PORT_KEY, port);

        logger.info("KafkaBroker server started successfully on port " + port);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (server != null) {
            logger.info("Shutting down KafkaBroker server");
            server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            logger.info("KafkaBroker server stopped");
        }
    }
}