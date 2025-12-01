package learning.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpirationMillis;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpirationMillis;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String extractUsername(String token) {
        Jwt jwt = decodeToken(token);
        return jwt.getSubject();
    }

    public <T> T extractClaim(String token, java.util.function.Function<Map<String, Object>, T> mapFn) {
        Jwt jwt = decodeToken(token);
        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) jwt.getClaims();
        return mapFn.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpirationMillis);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(Map.of(), userDetails, refreshExpirationMillis);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationMillis) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMillis);

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(userDetails.getUsername());

        if (extraClaims != null && !extraClaims.isEmpty()) {
            claimsBuilder.claims(c -> c.putAll(extraClaims));
        }

        JwtClaimsSet claims = claimsBuilder.build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtEncoderParameters params = JwtEncoderParameters.from(jwsHeader, claims);
        Jwt encoded = jwtEncoder.encode(params);
        return encoded.getTokenValue();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String username = jwt.getSubject();
            return username != null && username.equals(userDetails.getUsername());
        } catch (JwtException ex) {
            return false;
        }
    }

    private Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);
    }

}
