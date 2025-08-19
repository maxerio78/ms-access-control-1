package sunseries.travel.access.control.camel.router;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sunseries.travel.access.control.camel.component.permission.*;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import sunseries.travel.constant.EventType;
import sunseries.travel.constant.Kafka;
import sunseries.travel.constant.Origin;
import sunseries.travel.library.component.JsonStringToEventNotification;
import sunseries.travel.library.message.EventNotification;

@Slf4j
@Component
public class AccessControlRouterBuilder extends RouteBuilder {

    private static String BODY_TYPE = "${body.type} == ";

    @Autowired
    private CreatePermission createPermission;

    @Autowired
    private UpdatePermission updatePermission;

    @Autowired
    private UpdatePermissionForVAPI updatePermissionForVAPI;

    @Autowired
    private GetPermission searchPermission;

    @Autowired
    private VerifyPermission verifyPermission;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CreateHotelPermission createHotelPermission;

    @Autowired
    private GetPermissionByEmail getPermissionByEmail;
    
    private final JsonStringToEventNotification jsonStringToEventNotification = new JsonStringToEventNotification();
    {
        jsonStringToEventNotification.getAllows();
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .process((Exchange exchange) -> {
                    EventNotification eventNotification = new EventNotification();
                    String id = UUID.randomUUID().toString();
                    eventNotification.setId(id);
                    eventNotification.setTtid(id);
                    eventNotification.setOrigin(Origin.MS_ACCESS_CONTROL);
                    eventNotification.setType(EventType.EventLog.NEW_LOG.getEventType());
                    HashMap<String, Object> map = new HashMap<>();
                    Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    log.error("Router Error : " +  cause.getMessage(), cause);
                    map.put("message", "Application have a problem connection to MQ : " + cause.getMessage());
                    eventNotification.setEventData(map);
                    eventNotification.setDatetime(new Date());
                    producerTemplate.asyncRequestBody(Kafka.PRODUCER, eventNotification.toJSONString());
        });

        from(Kafka.CONSUMER)
            .bean(jsonStringToEventNotification)
            .choice()
                .when().simple(BODY_TYPE + "'" + EventType.AccessControl.MS_ACCESS_CONTROL_SEND_HOTEL_APPROVE_EMAIL.getEventType() + "'")
                    .bean(createHotelPermission)
                .when().simple(BODY_TYPE + "'" + EventType.AccessControl.CREATE_PERMISSION.getEventType() + "'")
                    .bean(createPermission)
                .when().simple(BODY_TYPE + "'" + EventType.AccessControl.REGISTRATION_AGENT_RESPONSE.getEventType() + "'")
                    .bean(createPermission)
                .when().simple(BODY_TYPE + "'" + EventType.AccessControl.UPDATE_PERMISSION.getEventType() + "'")
                    .bean(updatePermission)
                .when().simple(BODY_TYPE + "'" + EventType.AccessControl.GET_PERMISSION.getEventType() + "'")
                    .bean(searchPermission)
                .when().simple(BODY_TYPE + "'" + EventType.AccessControl.VERIFY_PERMISSION.getEventType() + "'")
                    .bean(verifyPermission)
                .when().simple(BODY_TYPE + "'" + EventType.AccessControl.SEARCH_USER_BY_EMAIL.getEventType() + "'")
                    .bean(getPermissionByEmail)
                .when().simple(BODY_TYPE + "'" + EventType.Users.UPDATE_USER_RESPONSE.getEventType() + "'")
                    .bean(updatePermissionForVAPI)
            .end();
    }

}
