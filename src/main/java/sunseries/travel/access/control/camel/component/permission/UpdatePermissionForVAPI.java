package sunseries.travel.access.control.camel.component.permission;

import static sunseries.travel.access.control.constant.Constant.PERMISSION_PREFIX;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import sunseries.travel.access.control.camel.component.MessageHandler;
import sunseries.travel.access.control.service.PermissionService;
import sunseries.travel.constant.EventType;
import sunseries.travel.library.message.EventNotification;
import sunseries.travel.library.utility.JsonUtil;
import sunseries.travel.model.access.control.Resource;
import sunseries.travel.model.access.control.UserPermission;

@Slf4j
@Component
public class UpdatePermissionForVAPI extends MessageHandler {
    private PermissionService permissionService;

    @Autowired
    @Lazy
    public UpdatePermissionForVAPI(PermissionService permissionService, ProducerTemplate producerTemplate) {
        super(producerTemplate);
        this.permissionService = permissionService;
    }

    public void performUpdate(EventNotification eventNotification) {
        Instant start = Instant.now();
        Map<String, Object> newEventData = new HashMap<>();
        try {
            String email = eventNotification.getEventData().get("email").toString();
            UserPermission permission = permissionService.findOne(PERMISSION_PREFIX + email.toLowerCase());
            if (permission == null) {
                createNotFoundException(newEventData);
            } else {
                //For VAPI
                if (eventNotification.getEventData().get("resources") != null) {
                    List<Resource> resources = JsonUtil.convertObject(eventNotification.getEventData().get("resources"), 
                            new TypeToken<List<Resource>>() {});
                    permission.setResources(resources);
                }
                permissionService.update(permission);
                newEventData.put("id", permission.getId());
                createSuccessResponse(newEventData);
            }
        } catch (Exception e) {
            createGeneralException(newEventData, e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            Instant end = Instant.now();
            raiseEvent(start, end, createEventResponse(eventNotification, newEventData, EventType.AccessControl.UPDATE_PERMISSION_RESPONSE.getEventType()));
        }
    }

}
