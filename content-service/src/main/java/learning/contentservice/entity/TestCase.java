package learning.contentservice.entity;

import lombok.Data;

@Data
public class TestCase {
    private String input;
    private String expectedOutput;
    private boolean isHidden;
}
