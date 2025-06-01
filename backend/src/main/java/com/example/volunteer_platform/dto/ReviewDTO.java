package com.example.volunteer_platform.dto;

import com.example.volunteer_platform.enums.ReviewType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private Long reviewerId;
    private Long revieweeId;
    private Integer rating;
    private String comment;
    private ReviewType reviewType;
    private LocalDateTime createdAt;
}