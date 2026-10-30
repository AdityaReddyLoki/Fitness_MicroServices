package com.Fitness.aiService.service;


import com.Fitness.aiService.model.Recommendation;
import com.Fitness.aiService.repository.RecommandRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommandService {
    private final RecommandRepo recommandRepo;


    public List<Recommendation> getUserRecommend(String userId) {

        return recommandRepo.findByUserId(userId);
    }

    public Recommendation getActivityRecommend(String activityId) {
        return recommandRepo.findByActivityId(activityId)
                .orElseThrow(() -> {
                    log.warn("No saved recommendation found (activityId={})", activityId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "No recommendation found for this activity");
                });
    }


}
