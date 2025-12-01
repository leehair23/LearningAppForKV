package learning.userservices.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String id;
    private String username;
    private String email;

    private String fullName;
    private String bio;
    private String avatarUrl;

    private Double score;
    private String rank;
    private Integer solvedCount;

    private String createdAt;
}
