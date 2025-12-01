package learning.userservices.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class UserProfile {
    @Id
    public String id;
    @Indexed(unique = true)
    @Field("uname")
    public String username;
    @Field("email")
    public String email;

    @Field("fn")
    public String fullName;
    @Field("bio")
    public String bio;
    @Field("avt")
    public String avtUrl;

    @Field("s")
    public Double score = 0.0;
    @Field("r")
    public String rank = "ROOKIE";
    @Field("Solved")
    public Integer solveCount = 0;

    public Instant createdAt = Instant.now();
    public Instant updatedAt = Instant.now();


}
