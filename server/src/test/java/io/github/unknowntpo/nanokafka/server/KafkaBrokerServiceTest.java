package io.github.unknowntpo.nanokafka.server;

import io.github.unknowntpo.nanokafka.server.grpc.KafkaBrokerServiceGrpc;
import io.github.unknowntpo.nanokafka.server.grpc.ProduceRequest;
import io.github.unknowntpo.nanokafka.server.grpc.ProduceResponse;
import io.github.unknowntpo.nanokafka.server.grpc.Record;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for KafkaBroker gRPC service.
 * The @WithKafkaBroker annotation automatically starts/stops the server.
 * Use @WithKafkaBroker(port = 9999) to specify a custom port.
 */
@WithKafkaBroker(port = KafkaBrokerServiceTest.SERVER_PORT)
class KafkaBrokerServiceTest {

    static final int SERVER_PORT = 50051;

    @Test
    void testProduceRequest() throws Exception {
        // Create a channel to the server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", SERVER_PORT)
            .usePlaintext()
            .build();

        try {
            // Create a stub
            KafkaBrokerServiceGrpc.KafkaBrokerServiceBlockingStub stub =
                KafkaBrokerServiceGrpc.newBlockingStub(channel);

            // Create a test record
            Record record = Record.newBuilder()
                .setKey("test-key-1")
                .setValue("test-value-1")
                .setTimestamp(System.currentTimeMillis())
                .build();

            // Create producer request
            ProduceRequest request = ProduceRequest.newBuilder()
                .setTopicName("test-topic")
                .setRecord(record)
                .build();

            // Send request
            ProduceResponse response = stub.produce(request);

            // Verify response
            assertTrue(response.getSuccess(), "Producer request should succeed");
            assertNotNull(response.getMessage());
            assertTrue(response.getMessage().contains("test-topic"));

            System.out.println("Test passed! Response: " + response.getMessage());

        } finally {
            // Shutdown channel
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}