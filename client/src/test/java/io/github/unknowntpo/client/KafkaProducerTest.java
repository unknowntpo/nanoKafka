package io.github.unknowntpo.client;

import io.github.unknowntpo.Record;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class KafkaProducerTest {
    @Test
    void testProduceRecord() {
        var record = new Record("key1", "value1");
        KafkaProducer producer = spy(KafkaProducer.class);
        producer.send("topic1", record);
        verify(producer).send("topic1", record);
    }
}
