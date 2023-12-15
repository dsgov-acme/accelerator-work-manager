package io.nuvalence.workmanager.service.service;

import io.nuvalence.events.brokerclient.config.PublisherProperties;
import io.nuvalence.events.event.NotificationEvent;
import io.nuvalence.events.event.service.EventGateway;
import io.nuvalence.workmanager.service.domain.transaction.Transaction;
import io.nuvalence.workmanager.service.events.EventFactory;
import io.nuvalence.workmanager.service.events.PublisherTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import jakarta.ws.rs.NotFoundException;

/**
 * Manages the communication with notification service.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SendNotificationService {

    private final PublisherProperties publisherProperties;
    private final EventGateway eventGateway;

    @Value("${dashboard.url}")
    private String dashboardUrl;

    private Map<String, String> createNotificationParameterMap(
            Map<String, String> camundaPropertyMap, Transaction transaction) {
        Map<String, String> propertyMap = new HashMap<>();
        camundaPropertyMap.forEach(
                (key, value) -> {
                    try {
                        if (key.equals("portal-url")) {
                            propertyMap.put("portal-url", dashboardUrl);
                        } else {
                            propertyMap.put(
                                    key, PropertyUtils.getProperty(transaction, value).toString());
                        }
                    } catch (Exception e) {
                        log.error("Error occurred getting value for property", e);
                    }
                });

        return propertyMap;
    }

    /**
     * Sends a notification request.
     *
     * @param transaction Transaction whose notification is to be sent.
     * @param notificationKey Key for the message template.
     * @param properties Properties to complete message template.
     */
    public void sendNotification(
            Transaction transaction, String notificationKey, Map<String, String> properties) {

        NotificationEvent notificationEvent =
                EventFactory.createNotificationEvent(
                        UUID.fromString(transaction.getSubjectUserId()),
                        notificationKey,
                        createNotificationParameterMap(properties, transaction));

        Optional<String> fullyQualifiedTopicNameOptional =
                publisherProperties.getFullyQualifiedTopicName(
                        PublisherTopic.NOTIFICATION_REQUEST.name());

        if (fullyQualifiedTopicNameOptional.isEmpty()) {
            throw new NotFoundException(
                    "Notification requests topic not found, topic name: "
                            + PublisherTopic.NOTIFICATION_REQUEST.name());
        }

        eventGateway.publishEvent(notificationEvent, fullyQualifiedTopicNameOptional.get());
    }
}
