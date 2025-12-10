package learning.submissionservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreUpdateRequest {
    private Double scoreToAdd;
    private Boolean incrementSolved;
    private String contestId;
}
