package com.Fitness.aiService.service;

import com.Fitness.aiService.repository.RecommandRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommandServiceTest {

    @Mock
    private RecommandRepo recommandRepo;

    @InjectMocks
    private RecommandService recommandService;

    @Test
    void missingActivityRecommendationReturnsNotFound() {
        String activityId = "6adf44dc6a6be1d3fc317698";
        when(recommandRepo.findByActivityId(activityId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recommandService.getActivityRecommend(activityId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
