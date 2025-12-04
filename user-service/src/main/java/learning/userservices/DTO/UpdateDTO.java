package learning.userservices.DTO;

import lombok.Data;

@Data
public class UpdateDTO {
    private String fullName;
    private String bio;
    private String avatarUrl;

    private String email;
    private String role;

    private Double score;
    private String rank;
}
