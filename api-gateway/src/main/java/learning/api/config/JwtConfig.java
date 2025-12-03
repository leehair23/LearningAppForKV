package learning.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {
    private static final Logger log =  LoggerFactory.getLogger(JwtConfig.class);
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(@Value("${spring.security.jwt.secret-key}") String base64Secret){
        log.info(base64Secret);
        byte[] secret = Base64.getDecoder().decode(base64Secret);
        SecretKey secretKey = new SecretKeySpec(secret, "HmacSHA256");

        return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
