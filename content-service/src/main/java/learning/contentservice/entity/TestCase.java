package learning.contentservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TestCase {
    private String input;
    private String expectedOutput;
    @JsonProperty("isHidden")
    private boolean isHidden;
}
