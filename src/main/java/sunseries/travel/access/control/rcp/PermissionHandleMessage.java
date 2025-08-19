package sunseries.travel.access.control.rcp;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sunseries.travel.access.control.repository.RedisRepository;
import sunseries.travel.access.control.service.PermissionService;
import sunseries.travel.java.rpc.v2.server.ARpcServerHandleMessage;
import sunseries.travel.library.message.EventNotification;
import sunseries.travel.model.access.control.UserPermission;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@DependsOn(value = {"permissionService", "redisRepository"})
public class PermissionHandleMessage extends ARpcServerHandleMessage {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    @Lazy
    private RedisRepository redisRepository;

    @Override
    public String handleMessage(Object o) {
        final Instant start = Instant.now();
        EventNotification eventNotification = getEventNotification(o);
        String email = eventNotification.getEventData().get("email").toString();
        Instant s2 = Instant.now();

        final String key = "permission::" + email.toLowerCase();
        String json = redisRepository.getData(key);
        UserPermission userPermission = null;
        if (StringUtils.isEmpty(json)) {
            userPermission = permissionService.findPermissionByEmail(email.toLowerCase());
            redisRepository.addData(key, new Gson().toJson(userPermission), 5 , TimeUnit.MINUTES);
        } else {
            userPermission = new Gson().fromJson(json, UserPermission.class);
        }

        log.debug("{}, find permissionByEmail, {}ms", eventNotification.getTtid(), ChronoUnit.MILLIS.between(s2, Instant.now()));
        if (userPermission != null) {
            eventNotification.getEventData().put("status", "SUCCESS");
            eventNotification.getEventData().put("permissions", userPermission);
        } else {
            eventNotification.getEventData().put("status", "FAILED");
        }
        log.debug("get permission by email {} : {} ==> processing time {} ms", eventNotification.getTtid(), eventNotification.getEventData().get("status"), ChronoUnit.MILLIS.between(start, Instant.now()));
        return eventNotification.toJSONString();
    }

    @Override
    public String responseQueue(Object o) {
        EventNotification eventNotification = getEventNotification(o);
        return eventNotification.getId();
    }

    /**
     *
     * @param o
     * @return
     */
    private EventNotification getEventNotification(Object o) {
        return new Gson().fromJson(o.toString(), EventNotification.class);
    }
}
