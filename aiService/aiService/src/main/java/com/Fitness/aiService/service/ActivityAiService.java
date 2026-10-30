
package com.Fitness.aiService.service;

import com.Fitness.aiService.model.Activity;
import com.Fitness.aiService.model.Recommendation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        log.info("Requesting Gemini recommendation (activityId={})", activity.getId());
        String prompt = CreatePromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("Received Gemini response (activityId={})", activity.getId());

        try {
            Recommendation recommendation = processAiResponse(activity, aiResponse);
            log.info("Parsed Gemini recommendation (activityId={})", activity.getId());
            return recommendation;
        } catch (RuntimeException e) {
            log.error("Failed to parse Gemini response (activityId={}, errorType={})",
                    activity.getId(), e.getClass().getSimpleName());
            throw e;
        }
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(aiResponse);

            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException("Gemini returned no candidates");
            }

            String json = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode data = mapper.readTree(json);
            JsonNode analysis = data.path("analysis");

            String recommendation =
                    "Overall: " + analysis.path("overall").asText()
                            + "\nPace: " + analysis.path("pace").asText()
                            + "\nHeart Rate: " + analysis.path("heartRate").asText()
                            + "\nCalories: " + analysis.path("caloriesBurned").asText();

            List<String> improvements = new ArrayList<>();
            data.path("improvements").forEach(item ->
                    improvements.add(item.path("area").asText() + ": "
                            + item.path("recommendations").asText()));

            List<String> suggestions = new ArrayList<>();
            data.path("suggestions").forEach(item ->
                    suggestions.add(item.path("workout").asText() + ": "
                            + item.path("description").asText()));

            List<String> safety = new ArrayList<>();
            data.path("safety").forEach(item ->
                    safety.add(item.asText()));

            Recommendation result = new Recommendation();
            result.setActivityId(activity.getId());
            result.setUserId(activity.getUserId());
            result.setActivityType(String.valueOf(activity.getType()));
            result.setRecommendation(recommendation);
            result.setImprovements(improvements);
            result.setSuggestions(suggestions);
            result.setSafety(safety);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to process Gemini response", e);
        }
    }

    private String CreatePromptForActivity(Activity activity) {
        return String.format("""
                Analyse this fitness activity and provide detailed recommendations in this JSON format:
                {
                  "analysis": {
                    "overall": "overall analysis here",
                    "pace": "pace analysis here",
                    "heartRate": "heartRate analysis here",
                    "caloriesBurned": "calories analysis here"
                  },
                  "improvements": [
                    {
                      "area": "Area Name",
                      "recommendations": "detailed recommendation"
                    }
                  ],
                  "suggestions": [
                    {
                      "workout": "workout name",
                      "description": "Detailed workout description"
                    }
                  ],
                  "safety": [
                    "safety point 1",
                    "safety point 2"
                  ]
                }

                Analyse this activity:
                Activity Type: %s
                Duration: %d minutes
                Calories Burned: %d
                Additional Metrics: %s

                Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety.
                Ensure the response follows the EXACT JSON format above.
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());
    }
}
