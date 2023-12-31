package io.nuvalence.workmanager.service.usermanagementapi;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.nuvalence.workmanager.service.usermanagementapi.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

class UserManagementClientTest {

    private UserManagementClient userManagementClient;

    @Mock private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userManagementClient = new UserManagementClient(restTemplate);
    }

    @Test
    void getUser_succesfulrequest() {
        UUID userId = UUID.randomUUID();
        User user =
                User.builder()
                        .id(userId)
                        .displayName("Federico Garcia")
                        .email("federico@nobody.com")
                        .build();
        when(restTemplate.exchange(
                        anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(User.class)))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        Optional<User> returnedUser = userManagementClient.getUser(userId);

        // Assert
        assertTrue(returnedUser.isPresent());
        verify(restTemplate, times(1))
                .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(User.class));
    }

    @Test
    void getUser_notfound() {
        UUID userId = UUID.randomUUID();
        when(restTemplate.exchange(
                        anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(User.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Optional<User> returnedUser = userManagementClient.getUser(userId);

        // Assert
        assertTrue(returnedUser.isEmpty());
        verify(restTemplate, times(1))
                .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(User.class));
    }
}
