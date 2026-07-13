package org.example.projeto.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8081}")
public interface AuthClient {

    @GetMapping("/api/auth/users/{id}")
    UserDTO getUserById(@PathVariable("id") String id);

    @GetMapping("/api/auth/users/email/{email}")
    UserDTO getUserByEmail(@PathVariable("email") String email);

    @GetMapping("/api/auth/users/{id}/exists")
    Boolean userExists(@PathVariable("id") String id);

    @GetMapping("/api/auth/users/{id}/role")
    String getUserRole(@PathVariable("id") String id);
}