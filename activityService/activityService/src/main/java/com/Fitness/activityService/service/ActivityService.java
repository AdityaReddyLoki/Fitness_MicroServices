package com.Fitness.activityService.service;

import com.Fitness.activityService.dto.ActivityRequest;
import com.Fitness.activityService.dto.ActivityResponse;
import com.Fitness.activityService.model.Activity;
import com.Fitness.activityService.repository.ActivityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor  //onstr for only the required fields unlike All args const
@Slf4j
public class ActivityService {

    private final ActivityRepo activityRepo;

    private final MongoTemplate mongoTemplate;
    private final RabbitTemplate rabbitTemplate;


    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final userValidationService userValidationService;

    public ActivityResponse trackActivity(ActivityRequest request, String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-ID is required");
        }

        boolean isValidUser = userValidationService.validateUser(userId);
        if (!isValidUser) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid authenticated user");
        }

        Activity activity= Activity.builder()
                .userId(userId)
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedAct=activityRepo.save(activity);

        try{
            rabbitTemplate.convertAndSend(exchange,routingKey,savedAct);
            log.info("Published activity to RabbitMQ (activityId={}, exchange={}, routingKey={})",
                    savedAct.getId(), exchange, routingKey);
        }catch (Exception e){
            log.error("Failed to publish activity to RabbitMQ (activityId={}, errorType={})",
                    savedAct.getId(), e.getClass().getSimpleName());
        }

        System.out.println("Saved activity ID: " + savedAct.getId());
        System.out.println("Database: " + activityRepo.findAll().size());
        System.out.println("DB: " + activityRepo.findAll());
        System.out.println("DB Name: " + mongoTemplate.getDb().getName());
        System.out.println("Count: " + mongoTemplate.getCollection("activities").countDocuments());
        return mapToResponse(savedAct);
    }
    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse res=new ActivityResponse();

        res.setId(activity.getId());
        res.setUserId(activity.getUserId());
        res.setType(activity.getType());
        res.setDuration(activity.getDuration());
        res.setCaloriesBurned(activity.getCaloriesBurned());
        res.setStartTime(activity.getStartTime());
        res.setAdditionalMetrics(activity.getAdditionalMetrics());
        res.setCreatedAt(activity.getCreatedAt());
        res.setUpdateAt(activity.getUpdateAt());
        return res;
    }

    public List<ActivityResponse> getActivity(String userId) {

        List<Activity> activityList= activityRepo.findByUserId(userId);
        return activityList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId) {
        return activityRepo.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Activity not found  "+activityId));
    }
}
