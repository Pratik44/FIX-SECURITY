package com.fixsecurity.ingestion;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fixsecurity.engine.ParsedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;

/**
 * Kafka producer for sending parsed FIX messages to Kafka topics
 */
public class KafkaMessageProducer {
    private KafkaProducer<String, String> producer;
    private ObjectMapper objectMapper;
    private String topicName;
    
    public KafkaMessageProducer(String bootstrapServers, String topicName) {
        this.topicName = topicName;
        this.objectMapper = new ObjectMapper();
        
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        
        this.producer = new KafkaProducer<>(props);
    }
    
    /**
     * Send a parsed FIX message to Kafka
     */
    public void sendMessage(ParsedMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            String key = message.getSenderCompID() + "-" + message.getMsgSeqNum();
            
            ProducerRecord<String, String> record = new ProducerRecord<>(
                topicName, 
                key, 
                messageJson
            );
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Error sending message: " + exception.getMessage());
                } else {
                    System.out.println("Message sent to topic: " + metadata.topic() + 
                                     ", partition: " + metadata.partition() + 
                                     ", offset: " + metadata.offset());
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to serialize message: " + e.getMessage());
        }
    }
    
    /**
     * Send raw FIX message string to Kafka
     */
    public void sendRawMessage(String rawFixMessage, String sessionId) {
        ProducerRecord<String, String> record = new ProducerRecord<>(
            "fix-messages-raw",
            sessionId,
            rawFixMessage
        );
        
        producer.send(record);
    }
    
    public void close() {
        producer.close();
    }
}
