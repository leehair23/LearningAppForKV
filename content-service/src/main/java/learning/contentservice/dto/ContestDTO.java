package learning.contentservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ContestDTO {
    private String id;

    private String title;
    private String description;

    private Instant startTime;
    private Instant endTime;

    private List<String> problemIds;
    private String status;
    private Long durationMinutes;
    private List<ProblemDTO> problems;
    private Integer participantCount;
}
