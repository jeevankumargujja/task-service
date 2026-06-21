package com.hashclick.taskservice.client;

import com.hashclick.taskservice.dto.UserResponse;
import com.hashclick.taskservice.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(RestTemplate restTemplate,
                             @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public UserResponse getUserById(Long id, String bearerToken) {
        try {
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    userServiceUrl + "/api/users/" + id,
                    HttpMethod.GET,
                    requestWithAuth(bearerToken),
                    UserResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
    }

    public UserResponse getUserByEmail(String email, String bearerToken) {
        try {
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    userServiceUrl + "/api/users/email/" + email,
                    HttpMethod.GET,
                    requestWithAuth(bearerToken),
                    UserResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found: " + email);
        }
    }

    private HttpEntity<Void> requestWithAuth(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken.replace("Bearer ", ""));
        return new HttpEntity<>(headers);
    }
}
