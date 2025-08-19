package sunseries.travel.access.control.rcp;

import sunseries.travel.java.rpc.v2.client.RpcClientV2;
import sunseries.travel.library.message.EventNotification;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestClient {

    public static void mainx(String[] args) throws InterruptedException, IllegalAccessException, InvocationTargetException {

        EventNotification eventNotification = new EventNotification();
        String id = UUID.randomUUID().toString();
        eventNotification.setId(id);
        eventNotification.setTtid(id);
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("token", "70c30599-677f-4d93-8e5f-89ac6d74fb55");
        eventNotification.setEventData(eventData);


        RpcClientV2 rpcClientV2 = new RpcClientV2("redis", 6379);
        String response = rpcClientV2.sendMessageAndReceiveMessage("permission-queue",
                eventNotification.getTtid(), 10,
                eventNotification.toJSONString());

        System.out.println("===>>> " + response);
    }
}
