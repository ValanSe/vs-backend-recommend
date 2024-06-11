package com.valanse.recommend.service.recommend;

import com.valanse.recommend.entity.FavoriteCategory;
import com.valanse.recommend.entity.User;
import com.valanse.recommend.entity.UserAnswer;
import com.valanse.recommend.entity.UserCategoryPreference;
import com.valanse.recommend.repository.jpa.FavoriteCategoryRepository;
import com.valanse.recommend.repository.jpa.UserAnswerRepository;
import com.valanse.recommend.repository.jpa.UserCategoryPreferenceRepository;
import com.valanse.recommend.repository.jpa.UserRepository;
import com.valanse.recommend.service.kafka.EventTypes;
import com.valanse.recommend.service.kafka.KafkaProducerService;
import com.valanse.recommend.util.DataParseUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendService {

    private final KafkaProducerService kafkaProducerService;
    private final UserCategoryPreferenceRepository userCategoryPreferenceRepository;
    private final FavoriteCategoryRepository favoriteCategoryRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserRepository userRepository;

    public void checkFavoriteCategoryChange(String data) {
        try {
            Map<String, String> dataMap = DataParseUtil.parseCommaSeparatedKeyValuePairs(data);

            Integer userId = Integer.valueOf(dataMap.get("userId"));
            String category = dataMap.get("category");

            List<UserCategoryPreference> allUCP = userCategoryPreferenceRepository.findAllByUserId(userId);
            if (allUCP.isEmpty()) {
                throw new EntityNotFoundException("UserCategoryPreferences not found for userId: " + userId);
            }

            // UCP 기반 내림차순 PQ
            PriorityQueue<UserCategoryPreference> pq = new PriorityQueue<>(
                    Comparator.comparingInt(UserCategoryPreference::getTotalPreference).reversed());

            pq.addAll(allUCP);

            UserCategoryPreference favoriteCategory = pq.peek();
            if (favoriteCategory == null) {
                throw new EntityNotFoundException("Favorite category not found for userId: " + userId);
            }

            FavoriteCategory preFavoriteCategory = favoriteCategoryRepository.findByUserId(userId);

            if (preFavoriteCategory == null || !(preFavoriteCategory.getCategory().equals(favoriteCategory.getCategory()))) {
                String fcData = String.format(
                        "userId:%d,category:%s",
                        favoriteCategory.getUserId(),
                        favoriteCategory.getCategory()
                );

                kafkaProducerService.sendChangeEvent(EventTypes.FAVORITE_CATEGORY_CHANGED, fcData);
            }

        } catch (EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error in checkFavoriteCategoryChange", e);
        }
    }

    public void recommendQuizzesBasedOnFavoriteCategory(String data) {
        try {
            Map<String, String> dataMap = DataParseUtil.parseCommaSeparatedKeyValuePairs(data);

            Integer userId = Integer.valueOf(dataMap.get("userId"));
            String category = dataMap.get("category");

            // Epsilon 값 설정 (0과 1 사이의 값)
            double epsilon = 0.7; // 엡실론 값으로 유저가 속한 집합 내의 선호 문제 추천

            // 랜덤 값 생성
            double randomValue = Math.random();

            List<Integer> recommendedQuizIds;

            if (randomValue < epsilon) {
                // Epsilon의 확률로 유저가 속한 집합 내 다른 유저들이 선호한 문제를 랜덤하게 추천
                List<Integer> similarUserIds = favoriteCategoryRepository.findAllByCategory(category)
                        .stream()
                        .map(FavoriteCategory::getUserId)
                        .toList();

                // 최대 10개의 퀴즈만 가져오도록 페이지 요청 설정
                PageRequest pageRequest = PageRequest.of(0, 10);

                recommendedQuizIds = userAnswerRepository.findRecommendedQuizzes(userId, similarUserIds, pageRequest);
                log.info("Recommended quizzes for user_id: {} based on favorite category: {} are: {}", userId, category, recommendedQuizIds);
            } else {
                // (1-Epsilon)의 확률로 유저가 속하지 않은 다른 집합 내 유저들이 선호한 문제를 랜덤하게 추천
                List<Integer> otherUserIds = userRepository.findAll()
                        .stream()
                        .map(User::getUserId)
                        .filter(id -> !id.equals(userId))
                        .toList();
                // 최대 10개의 퀴즈만 가져오도록 페이지 요청 설정
                PageRequest pageRequest = PageRequest.of(0, 10);

                recommendedQuizIds = userAnswerRepository.findRecommendedQuizzesFromOtherUsers(userId, otherUserIds, pageRequest);
                log.info("Recommended quizzes for user_id: {} from other users are: {}", userId, recommendedQuizIds);
            }

            String recommendedQuizIdsStr = recommendedQuizIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            String rqData = String.format("userId:%d,recommendedQuizIds:%s", userId, recommendedQuizIdsStr);

            kafkaProducerService.sendChangeEvent(EventTypes.RECOMMEND_QUIZ_CHANGED, rqData);

        } catch (Exception e) {
            log.error("Error recommending quizzes based on favorite category", e);
        }

    }
}
