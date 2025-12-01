package learning.auth.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import learning.auth.entity.AuthenticationRequest;
import learning.auth.entity.AuthenticationResponse;

import learning.auth.entity.RegisterRequest;
import learning.auth.entity.Role;
import learning.auth.entity.User;
import learning.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthencationService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // --- 1. ĐĂNG KÝ (Username/Pass) ---
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // Gọi hàm helper để tạo token có chứa email
        return generateAuthResponse(user);
    }

    // --- 2. ĐĂNG NHẬP (Username/Pass) ---
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        return generateAuthResponse(user);
    }

    // --- 3. ĐĂNG NHẬP GOOGLE ---
    public AuthenticationResponse authenticateGoogle(String googleToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleToken);

            // SỬA LỖI LOGIC: idToken != null mới xử lý
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                // Lấy tên làm username, xử lý trùng lặp nếu cần (hoặc dùng email làm username luôn)
                String name = (String) payload.get("name");
                String username = email; // Tạm thời dùng email làm username cho unique

                var user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                    user = User.builder()
                            .username(username)
                            .email(email)
                            .password(passwordEncoder.encode("GG_AUTH_NO_PASS"))
                            .role(Role.USER)
                            .build();
                    userRepository.save(user);
                }

                return generateAuthResponse(user);
            } else {
                throw new RuntimeException("Invalid Google Token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Google Auth Failed", e);
        }
    }

    // --- 4. REFRESH TOKEN ---
    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken); // Token cũ không có email trong subject, chỉ có username

        if (username != null) {
            var user = userRepository.findByUsername(username).orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                // Tạo Access Token mới có chứa email
                var accessToken = jwtService.generateToken(getExtraClaims(user), user);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        return null;
    }

    // --- HELPER METHODS (Tái sử dụng code) ---

    // Tạo Map chứa thông tin phụ (Email, Role) để nhét vào Token
    private Map<String, Object> getExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", user.getEmail());
        extraClaims.put("role", user.getRole().name());
        return extraClaims;
    }

    // Sinh Response chứa Access & Refresh Token
    private AuthenticationResponse generateAuthResponse(User user) {
        var jwtToken = jwtService.generateToken(getExtraClaims(user), user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
