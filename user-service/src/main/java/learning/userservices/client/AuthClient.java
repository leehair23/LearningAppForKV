package learning.userservices.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="auth-service")
public interface AuthClient {
    @PutMapping("/auth/users/{id}/status")
    void changeStatus(@PathVariable("id") String id, @RequestParam("enabled") boolean enabled);

    @PutMapping("/auth/users/{id}")
    void updateAuthInfo(@PathVariable("id") String id, Map<String, String> authUpdates);

    @DeleteMapping("/auth/users/{id}")
    void deleteUser(@PathVariable("id")String id);
}
