package com.valanse.recommend.service.kafka;

import com.valanse.recommend.service.recommend.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RecommendService recommendService;
    private static final String SERVER_NAME_CONSUME = "valanse-api";

    @KafkaListener(topics = SERVER_NAME_CONSUME)
    public void consume(String message) {
        String[] parts = message.split("\\|", 2);
        String eventType = parts[0];
        String data = parts[1];

        switch (eventType) {
            case EventTypes.USER_CATEGORY_PREFERENCE_CHANGED:
                handleUserCategoryPreferenceChange(data);
                return;

            case EventTypes.FAVORITE_CATEGORY_CHANGED:
                handleFavoriteCategoryChange(data);
                return;

            default:
                // Handle unknown event types
                return;
        }
    }

    private void handleUserCategoryPreferenceChange(String data) {
        try {
            recommendService.checkFavoriteCategoryChange(data);

        } catch (Exception e) {

        }

    }

    private void handleFavoriteCategoryChange(String data) {
        try {
            recommendService.recommendQuizzesBasedOnFavoriteCategory(data);

        } catch (Exception e) {

        }
    }


}
