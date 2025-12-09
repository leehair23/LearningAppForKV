package learning.auth.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learning.auth.entity.AuthenticationRequest;
import learning.auth.entity.AuthenticationResponse;
import learning.auth.entity.RegisterRequest;
import learning.auth.entity.Role;
import learning.auth.services.AuthencationService;
import learning.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthencationService service;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse>register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> loginGoogle(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        return ResponseEntity.ok(service.authenticateGoogle(token));
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request){
        AuthenticationResponse response = service.refreshToken(request);
        if(response == null) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/signout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok("logout successfully");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body){
        service.forgotPassword(body.get("email"));
        return ResponseEntity.ok("Reset link has been sent to your email");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");
        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Missing info");
        }
        service.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
    }

    //user-service stuff (CRUD)
    @PutMapping("/users/{username}/status")
    public ResponseEntity<?> changeStatus(@PathVariable String username, @RequestParam boolean enabled){
        var user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("username not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<?>updateUser(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ){
        var user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Not found " + id));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("role")) user.setRole(Role.valueOf(body.get("role")));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id){
        var user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Not found " + id));
        userRepository.delete(user);
        return ResponseEntity.ok().build();
    }
}
