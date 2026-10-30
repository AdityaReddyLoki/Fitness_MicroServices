package com.Fitness.aiService.controller;


import com.Fitness.aiService.model.Recommendation;
import com.Fitness.aiService.service.RecommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommandController {

    private final RecommandService recommandService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommend(@PathVariable String userId){
        return ResponseEntity.ok((recommandService.getUserRecommend(userId)));
    }
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommend(@PathVariable String activityId){
        return ResponseEntity.ok((recommandService.getActivityRecommend(activityId)));
    }



}
