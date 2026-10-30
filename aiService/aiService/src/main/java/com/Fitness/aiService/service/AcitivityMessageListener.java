package com.Fitness.aiService.service;

import com.Fitness.aiService.model.Activity;
import com.Fitness.aiService.model.Recommendation;
import com.Fitness.aiService.repository.RecommandRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AcitivityMessageListener {
    private final RecommandRepo recommandRepo;
    private final ActivityAiService activityAiService;

    @RabbitListener(queues = "activity.queue")
    @Transactional
    public void processActivity(Activity activity){
        log.info("Received activity from RabbitMQ (activityId={})", activity.getId());
        try {
            Recommendation recommendation =
                    activityAiService.generateRecommendation(activity);
            log.info("Generated recommendation (activityId={})", activity.getId());

            Recommendation savedRecommendation = recommandRepo.save(recommendation);
            log.info("Saved recommendation (activityId={}, recommendationId={})",
                    activity.getId(), savedRecommendation.getId());
        } catch (RuntimeException e) {
            log.error("Activity recommendation processing failed (activityId={}, errorType={})",
                    activity.getId(), e.getClass().getSimpleName());
            throw e;
        }
    }
}
