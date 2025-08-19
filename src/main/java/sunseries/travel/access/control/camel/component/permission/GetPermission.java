package sunseries.travel.access.control.camel.component.permission;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;
import sunseries.travel.access.control.camel.component.MessageHandler;
import sunseries.travel.access.control.service.PermissionService;
import sunseries.travel.constant.EventType;
import sunseries.travel.library.message.EventNotification;
import sunseries.travel.library.message.GsonUTCDateAdapter;
import sunseries.travel.model.access.control.UserPermission;

@Slf4j
@Component
public class GetPermission extends MessageHandler {
    private PermissionService permissionService;

    @Autowired
    @Lazy
    public GetPermission(PermissionService permissionService, ProducerTemplate producerTemplate) {
        super(producerTemplate);
        this.permissionService = permissionService;
    }

    public void performGet(EventNotification eventNotification) {
        Instant start = Instant.now();
        Map<String, Object> newEventData = new HashMap<>();
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
            String eventData = gson.toJson(eventNotification.getEventData());
            UserPermission permission = gson.fromJson(eventData, UserPermission.class);
            UserPermission userPermission = permissionService.findPermissionByEmail(permission.getEmail());
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
            raiseEvent(start, end, createEventResponse(eventNotification, newEventData, EventType.AccessControl.GET_PERMISSION_RESPONSE.getEventType()));
        }
    }
}
