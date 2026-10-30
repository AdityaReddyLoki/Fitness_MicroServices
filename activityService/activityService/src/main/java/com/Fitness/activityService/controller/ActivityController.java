package com.Fitness.activityService.controller;


import com.Fitness.activityService.dto.ActivityRequest;
import com.Fitness.activityService.dto.ActivityResponse;
import com.Fitness.activityService.model.Activity;
import com.Fitness.activityService.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {


    private ActivityService activityService;

    @PostMapping()
    public ResponseEntity<ActivityResponse> trackActivity(
            @RequestBody ActivityRequest request,
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(activityService.trackActivity(request, userId));
    }
    @GetMapping()        // we use list cause single user will hv the multiple activities (he can run some time ,sprint)
    public ResponseEntity<List<ActivityResponse>> getActivity(@RequestHeader("X-User-ID") String userId){  // requestHeader will bring the userId and
        return ResponseEntity.ok(activityService.getActivity(userId));                                       // will populate that id in the String's id
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }
}
