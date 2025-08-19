package sunseries.travel.access.control.camel.component.permission;

import static sunseries.travel.access.control.constant.Constant.PERMISSION_PREFIX;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class VerifyPermission extends MessageHandler {
    private PermissionService permissionService;

    @Autowired
    @Lazy
    public VerifyPermission(PermissionService permissionService, ProducerTemplate producerTemplate) {
        super(producerTemplate);
        this.permissionService = permissionService;
    }

    public void performVerify(EventNotification eventNotification) {
        Instant start = Instant.now();
        Map<String, Object> newEventData = eventNotification.getEventData();
        try {
            String email = eventNotification.getEventData().get("email").toString();
            String resources = eventNotification.getEventData().get("resources").toString();
            UserPermission userPermission = permissionService.findOne(PERMISSION_PREFIX + email.toLowerCase());
            if (userPermission == null) {
                createNotFoundException(newEventData);
            } else {
                List<Resource> allowedResources = getAllowedResources(resources, userPermission.getResources());
                newEventData.put("resources", allowedResources);
                createSuccessResponse(newEventData);
            }
        } catch (Exception e) {
            createGeneralException(newEventData, e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            Instant end = Instant.now();
            raiseEvent(start, end, createEventResponse(eventNotification, newEventData, EventType.AccessControl.VERIFY_PERMISSION_RESPONSE.getEventType()));
        }
    }

    private List<Resource> getAllowedResources(String resources, List<Resource> resourceList) {
        Type typeOfListString = new TypeToken<List<String>>() {}.getType();
        List<String> checkResourceList = JsonUtil.fromJson(resources, typeOfListString);
        List<Resource> allowedResourceList = resourceList.stream()
            .filter(resource -> checkResourceList.contains(resource.getName()))
            .collect(Collectors.toList());
        return allowedResourceList;
    }

}
