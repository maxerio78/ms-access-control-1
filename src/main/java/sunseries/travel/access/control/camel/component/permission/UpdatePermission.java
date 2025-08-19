package sunseries.travel.access.control.camel.component.permission;

import static sunseries.travel.access.control.constant.Constant.PERMISSION_PREFIX;

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
import sunseries.travel.library.utility.DateUtils;
import sunseries.travel.model.access.control.UserPermission;

@Slf4j
@Component
public class UpdatePermission extends MessageHandler {
    private PermissionService permissionService;

    @Autowired
    @Lazy
    public UpdatePermission(PermissionService permissionService, ProducerTemplate producerTemplate) {
        super(producerTemplate);
        this.permissionService = permissionService;
    }

    public void performUpdate(EventNotification eventNotification) {
        Instant start = Instant.now();
        Map<String, Object> newEventData = new HashMap<>();
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
            String eventData = gson.toJson(eventNotification.getEventData());
            UserPermission newPermission = gson.fromJson(eventData, UserPermission.class);
            newPermission.setId(PERMISSION_PREFIX + newPermission.getEmail().toLowerCase());
            UserPermission permission = permissionService.findOne(newPermission.getId());
            if (permission == null) {
                createNotFoundException(newEventData);
            } else {
                permission.setEmail(newPermission.getEmail());
                permission.setResources(newPermission.getResources());
                permission.setLastUpdatedDate(DateUtils.currentISODateWithUTC());
                permission.setRoleTemplates(newPermission.getRoleTemplates());
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
