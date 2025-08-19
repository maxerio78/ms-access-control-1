package sunseries.travel.access.control.camel.component.permission;

import java.time.Instant;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sunseries.travel.access.control.camel.component.MessageHandler;
import sunseries.travel.access.control.service.PermissionService;
import sunseries.travel.constant.EventType;
import sunseries.travel.library.message.EventNotification;
import sunseries.travel.model.access.control.UserPermission;

@Slf4j
@Component
public class GetPermissionByEmail extends MessageHandler {
    private PermissionService permissionService;

    @Autowired
    @Lazy
    public GetPermissionByEmail(PermissionService permissionService, ProducerTemplate producerTemplate) {
        super(producerTemplate);
        this.permissionService = permissionService;
    }

    public void getPermission(EventNotification eventNotification) {
        final Instant start = Instant.now();
        Map<String, Object> newEventData = eventNotification.getEventData();
        try {
            String email = eventNotification.getEventData().get("email").toString();
            UserPermission userPermission = permissionService.findPermissionByEmail(email);
            if (userPermission == null) {
                newEventData.put("permission", new UserPermission());
            } else {
                newEventData.put("permission", userPermission);
            }
            createSuccessResponse(newEventData);
        } catch (Exception e) {
            createGeneralException(newEventData, e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            Instant end = Instant.now();
            raiseEvent(start, end, createEventResponse(eventNotification, newEventData, EventType.AccessControl.GET_PERMISSION_BY_EMAIL_RESPONSE.getEventType()));
        }
    }

}
