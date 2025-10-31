package io.github.unknowntpo.client;

import io.github.unknowntpo.Record;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class KafkaProducerTest {
    @Test
    void testProduceRecord() {
        var record = new Record("key1", "value1", Instant.now().toEpochMilli());
        KafkaProducer _producer = new KafkaProducer();
        KafkaProducer producer = spy(_producer);
        producer.send("topic1", record);
        verify(producer).send("topic1", record);
    }
}
