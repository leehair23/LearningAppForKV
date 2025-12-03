package learning.userservices.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreUpdate {
    private Double scoreToAdd;
    private Boolean incrementSolved;
}
