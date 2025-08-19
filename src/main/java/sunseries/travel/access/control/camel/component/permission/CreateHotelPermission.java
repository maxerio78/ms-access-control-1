package sunseries.travel.access.control.camel.component.permission;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sunseries.travel.access.control.camel.component.MessageHandler;
import sunseries.travel.access.control.service.PermissionService;
import sunseries.travel.constant.EventType;
import sunseries.travel.library.message.EventNotification;
import sunseries.travel.library.utility.JsonUtil;
import sunseries.travel.model.user.User;

@Slf4j
@Component
public class CreateHotelPermission extends MessageHandler {
    private PermissionService permissionService;
    private RedisTemplate<String, String> redisClusterTemplate;

    @Autowired
    public CreateHotelPermission(PermissionService permissionService, ProducerTemplate producerTemplate, RedisTemplate<String, String> redisClusterTemplate) {
        super(producerTemplate);
        this.permissionService = permissionService;
        this.redisClusterTemplate = redisClusterTemplate;
    }

    public void performCreate(EventNotification eventNotification) throws Exception {
        final Instant start = Instant.now();
        final Map<String, Object> newEventData = new HashMap<String, Object>();
        try {
            final User user = JsonUtil.convertObject(eventNotification.getEventData().get("user"), User.class);
            permissionService.createUpdateUserPermission(user.getEmail(), eventNotification.getUserId());
            addEventDatatoRedis(eventNotification);
            createSuccessResponse(newEventData);
        }
        catch (Exception e) {
            createGeneralException(newEventData, e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            raiseEvent(start, Instant.now(), createEventResponse(eventNotification, newEventData, EventType.Auth.MS_AUTH_SEND_HOTEL_APPROVE_EMAIL.getEventType()));
        }
    }

    @Retryable(value = {RedisSystemException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
    private void addEventDatatoRedis(EventNotification eventNotification) {
        String referenceId = "emailToHotel::" + eventNotification.getTtid();
        eventNotification.setReferenceId(referenceId);
        String data = JsonUtil.toJson(eventNotification.getEventData());
        redisClusterTemplate.opsForValue().set(referenceId, data, 30, TimeUnit.SECONDS);
    }
}
