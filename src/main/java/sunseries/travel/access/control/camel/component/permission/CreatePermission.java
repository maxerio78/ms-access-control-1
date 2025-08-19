package sunseries.travel.access.control.camel.component.permission;

import static sunseries.travel.access.control.constant.Constant.PERMISSION_PREFIX;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import sunseries.travel.access.control.camel.component.MessageHandler;
import sunseries.travel.access.control.service.PermissionService;
import sunseries.travel.constant.EventType;
import sunseries.travel.library.message.EventNotification;
import sunseries.travel.library.message.GsonUTCDateAdapter;
import sunseries.travel.library.utility.BeanUtils;
import sunseries.travel.library.utility.DateUtils;
import sunseries.travel.library.utility.JsonUtil;
import sunseries.travel.model.access.control.Resource;
import sunseries.travel.model.access.control.UserPermission;

@Slf4j
@Component
public class CreatePermission extends MessageHandler {
    private PermissionService permissionService;

    @Autowired
    public CreatePermission(PermissionService permissionService, ProducerTemplate producerTemplate) {
        super(producerTemplate);
        this.permissionService = permissionService;
    }

    public void performCreate(EventNotification eventNotification) {
        Instant start = Instant.now();
        Map<String, Object> newEventData = new HashMap<>();
        try {
            if (isUserCreateFailed(eventNotification, newEventData)) {
                return;
            }
            final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
            final String eventData = gson.toJson(eventNotification.getEventData());
            final UserPermission permission = gson.fromJson(eventData, UserPermission.class);
            permission.setId(PERMISSION_PREFIX + permission.getEmail().toLowerCase());
            final UserPermission isExistUser = permissionService.findOne(permission.getId());
            if (isExistUser != null) {
                createDuplicateResponse(newEventData);
            } else {
                //For VAPI
                if (eventNotification.getEventData().get("resources") != null) {
                    List<Resource> resources = JsonUtil.convertObject(eventNotification.getEventData().get("resources"), 
                            new TypeToken<List<Resource>>() {});
                    permission.setResources(resources);
                }

                createPermission(newEventData, permission);
                setAgentIdToResponseData(eventNotification, newEventData);
            }
        } catch (JsonSyntaxException e) {
            createGeneralException(newEventData, e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            Instant end = Instant.now();
            raiseEvent(start, end, createEventResponse(eventNotification, newEventData, EventType.AccessControl.CREATE_USER_AND_PERMISSION_RESPONSE.getEventType()));
            log.info("create new permission {} : {} ==> processing time {} ms", eventNotification.getTtid(), newEventData.get("status").toString(), ChronoUnit.MILLIS.between(start, Instant.now()));
        }
    }
    
    private void setAgentIdToResponseData(EventNotification eventNotification, Map<String, Object> newEventData) {
        final String agentId = BeanUtils.safeGetter(() -> eventNotification.getEventData().get("agent_id").toString()).orElse(null);
        if (agentId != null) {
            newEventData.put("agent_id", agentId);
        }
    }

    private boolean isUserCreateFailed(EventNotification eventNotification, Map<String, Object> newEventData) {
        try {
            final Object createUserStatusObj = eventNotification.getEventData().get("status");
            if (createUserStatusObj.toString().equalsIgnoreCase("FAILURE")) {
                final Object message = eventNotification.getEventData().get("message");
                createGeneralException(newEventData, message == null ? "" : message.toString());
                return true;
            }
        } catch (NullPointerException e) {
            return true;
        }
        return false;
    }

    private void createPermission(Map<String, Object> newEventData, UserPermission permission) {
        permission.setCreatedDate(DateUtils.currentISODateWithUTC());
        permissionService.create(permission);
        newEventData.put("id", permission.getId());
        createSuccessResponse(newEventData);
    }

}
