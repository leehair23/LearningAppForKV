package learning.userservices.controller;

import learning.userservices.DTO.Response;
import learning.userservices.DTO.ScoreUpdate;
import learning.userservices.DTO.UpdateDTO;
import learning.userservices.DTO.UpdateRequest;
import learning.userservices.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            @RequestHeader("X-Auth-UserId") String userId,
            @RequestHeader("X-Auth-User") String username,
            @RequestHeader(value = "X-Auth-Email", required = false) String email
    ) {
        return ResponseEntity.ok(userService.getOrCreateProfile(userId,username, email));
    }

    @PutMapping("/me")
    public ResponseEntity<Response> updateProfile(
            @RequestHeader("X-Auth-UserId") String id,
            @RequestBody UpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<Response> getPublicProfile(@PathVariable String id){
        return ResponseEntity.ok(userService.getPublicProfile(id));
    }

    @PatchMapping("/{id}/score")
    public ResponseEntity<Void> updateScore(@PathVariable String id, @RequestBody ScoreUpdate request){
        userService.updateScore(id, request);
        return ResponseEntity.ok().build();
    }

    //admin
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
    ){
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(userService.searchUsers(q, pageable));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateUser(
            @PathVariable String id,
            @RequestBody UpdateDTO req,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
            ){
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(userService.updateUser(id, req));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
    ){
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable String id,
            @RequestParam boolean active,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER")  String role
    ){
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        userService.banUser(id, active);
        return ResponseEntity.ok().build();
    }

}