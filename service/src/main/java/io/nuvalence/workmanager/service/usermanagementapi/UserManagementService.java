package io.nuvalence.workmanager.service.usermanagementapi;

import io.nuvalence.workmanager.service.config.exceptions.ApiException;
import io.nuvalence.workmanager.service.usermanagementapi.models.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Client to interface with User Management API.
 */
public interface UserManagementService {

    /**
     * Get user.
     *
     * @param userId id of the user.
     * @return User.
     * @throws ApiException for possible errors reaching user management service.
     */
    Optional<User> getUser(UUID userId);
}
