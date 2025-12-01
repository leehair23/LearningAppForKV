package learning.userservices.controller;

import learning.userservices.DTO.Response;
import learning.userservices.DTO.ScoreUpdate;
import learning.userservices.DTO.UpdateRequest;
import learning.userservices.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<Response> getMyProfile(
            @RequestHeader("X-Auth-User") String username,
            @RequestHeader(value = "X-Auth-Email", required = false) String email
    ) {
        return ResponseEntity.ok(userService.getOrCreateProfile(username, email));
    }

    @PutMapping("/me")
    public ResponseEntity<Response> updateProfile(
            @RequestHeader("X-Auth-User") String username,
            @RequestBody UpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(username, request));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<Response> getPublicProfile(@PathVariable String username){
        return ResponseEntity.ok(userService.getPublicProfile(username));
    }

    @PatchMapping("/{username}/score")
    public ResponseEntity<Void> updateScore(@PathVariable String username, @RequestBody ScoreUpdate request){
        userService.updateScore(username, request);
        return ResponseEntity.ok().build();
    }
}