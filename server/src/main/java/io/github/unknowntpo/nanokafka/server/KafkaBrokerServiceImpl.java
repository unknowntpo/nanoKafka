package io.github.unknowntpo.nanokafka.server;

import io.github.unknowntpo.nanokafka.server.grpc.KafkaBrokerServiceGrpc;
import io.github.unknowntpo.nanokafka.server.grpc.ProduceRequest;
import io.github.unknowntpo.nanokafka.server.grpc.ProduceResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * Implementation of the KafkaBrokerService gRPC service.
 * Handles producer requests to write records to topics.
 */
public class KafkaBrokerServiceImpl extends KafkaBrokerServiceGrpc.KafkaBrokerServiceImplBase {
    private static final Logger logger = Logger.getLogger(KafkaBrokerServiceImpl.class.getName());

    // In-memory storage for topics (Phase 1: in-memory implementation)
    private final ConcurrentMap<String, io.github.unknowntpo.LogSegment> topics = new ConcurrentHashMap<>();

    @Override
    public void produce(ProduceRequest request, StreamObserver<ProduceResponse> responseObserver) {
        try {
            String topicName = request.getTopicName();
            io.github.unknowntpo.nanokafka.server.grpc.Record grpcRecord = request.getRecord();

            logger.info("Received producer request for topic: " + topicName);

            // Get or create topic's log segment
            io.github.unknowntpo.LogSegment logSegment = topics.computeIfAbsent(
                topicName,
                k -> new io.github.unknowntpo.LogSegment(topicName, new java.util.ArrayList<>())
            );

            // Convert gRPC Record to internal Record
            io.github.unknowntpo.Record record = new io.github.unknowntpo.Record(
                grpcRecord.getKey(),
                grpcRecord.getValue(),
                grpcRecord.getTimestamp()
            );

            // Append record to log segment
            logSegment.appendRecord(record);

            logger.info("Successfully appended record to topic: " + topicName +
                       ", total records: " + logSegment.recordSize());

            // Build success response
            ProduceResponse response = ProduceResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record appended successfully to topic: " + topicName)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.severe("Error processing producer request: " + e.getMessage());

            ProduceResponse response = ProduceResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Error: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}