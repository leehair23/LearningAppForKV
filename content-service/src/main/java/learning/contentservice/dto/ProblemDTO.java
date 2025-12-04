package learning.contentservice.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProblemDTO {
    private String id;
    private String title;
    private String description;
    private String difficulty;
    private List<String> tags;
    private Double timeLimit;
    private Long memoryLimit;
    private Map<String, String> templates;

    private List<TestCaseDTO> sampleTestcases;
}
