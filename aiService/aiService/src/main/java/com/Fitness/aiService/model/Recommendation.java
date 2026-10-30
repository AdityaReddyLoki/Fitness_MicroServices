package com.Fitness.aiService.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recommendations")
@Data
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activityId;
    private String userId;
    private String activityType;
    @Column(columnDefinition = "LONGTEXT")
    private String recommendation;

    @ElementCollection
    @CollectionTable(
            name = "recommendation_improvements",
            joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "improvements", columnDefinition = "LONGTEXT")
    private List<String> improvements;

    @ElementCollection
    @CollectionTable(
            name = "recommendation_suggestions",
            joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "suggestions", columnDefinition = "LONGTEXT")
    private List<String> suggestions;

    @ElementCollection
    @CollectionTable(
            name = "recommendation_safety",
            joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "safety", columnDefinition = "LONGTEXT")
    private List<String> safety;

    @CreationTimestamp
    private LocalDateTime createdAt;
}