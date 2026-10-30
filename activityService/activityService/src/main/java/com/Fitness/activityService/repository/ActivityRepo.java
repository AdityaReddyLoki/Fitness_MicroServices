package com.Fitness.activityService.repository;

import com.Fitness.activityService.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityRepo extends MongoRepository<Activity,String> {


    List<Activity> findByUserId(String userId);
}
