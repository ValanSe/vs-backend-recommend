package com.valanse.recommend.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String SERVER_NAME = "valanse-recommend";

    public void sendChangeEvent(String eventType, String data) {
        String message = String.format("%s|%s", eventType, data);
        kafkaTemplate.send(SERVER_NAME, message);
        log.info("Sent message: {}", message);
    }
}