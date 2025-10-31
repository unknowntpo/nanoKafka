package io.github.unknowntpo.server;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to start KafkaBroker server for integration tests.
 * Apply this to test classes that need a running gRPC server.
 * Server starts before all tests and stops after all tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(KafkaBrokerExtension.class)
public @interface WithKafkaBroker {
    /**
     * The port to start the server on. Defaults to 50051.
     */
    int port() default 50051;
}