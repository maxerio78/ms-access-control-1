//package sunseries.travel.access.control.camel.component;
//
//import com.google.gson.Gson;
//import org.apache.camel.ProducerTemplate;
//import org.junit.Assert;
//import org.junit.Test;
//import sunseries.travel.access.control.domain.db.User;
//import sunseries.travel.access.control.domain.message.EventNotification;
//import sunseries.travel.access.control.service.PermissionService;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class VerifyPermissionTest {
//
//    @Test
//    public void testVerifySuccess() {
//        VerifyPermission verifyPermission = new VerifyPermission();
//
//        PermissionService permissionService = mock(PermissionService.class);
//        ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
//
//        verifyPermission.setPermissionService(permissionService);
//        verifyPermission.setProducerTemplate(producerTemplate);
//
//        User permission = new User();
//        permission.setId("1111");
//        permission.setEmail("pea.chiwa@sunseries.travel");
//        permission.setResource("admin");
//        permission.setPermissions(new ArrayList<String>(
//                Arrays.asList("ADMIN_SYSTEM")));
//        when(permissionService.findOne(any())).thenReturn(permission);
//
//
//        String requestBody = "{\n" +
//                "\t\"id\": \"0eb945a4-0ee5-4527-8294-e3ab905ee0f7\",\n" +
//                "\t\"type\": \"verify_permission\",\n" +
//                "\t\"ttid\": \"1234567890123\",\n" +
//                "\t\"origin\": \"sunseries_access_control_client\",\n" +
//                "\t\"datetime\": \"2016-09-13T08:19:58Z\",\n" +
//                "\t\"event_data\": {\n" +
//                "\t\t\"email\": \"pea.chiwa@sunseries.travel\",\n" +
//                "\t\t\"resource\": \"admin\",\n" +
//                "\t\t\"role\": \"ADMIN_SYSTEM\"\n" +
//                "\t}\n" +
//                "}";
//
//        EventNotification eventNotification = new Gson().fromJson(requestBody, EventNotification.class);
//        eventNotification = verifyPermission.performVerifyPermission(eventNotification);
//
//        System.out.println(eventNotification);
//        Assert.assertEquals("SUCCESS", eventNotification.getEventData().get("status"));
//
//    }
//
//    @Test
//    public void testVerifyPermissionDenie() {
//        VerifyPermission verifyPermission = new VerifyPermission();
//
//        PermissionService permissionService = mock(PermissionService.class);
//        ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
//
//        verifyPermission.setPermissionService(permissionService);
//        verifyPermission.setProducerTemplate(producerTemplate);
//
//        User permission = new User();
//        permission.setId("1111");
//        permission.setEmail("pea.chiwa@sunseries.travel");
//        permission.setResource("admin");
//        permission.setPermissions(new ArrayList<String>(
//                Arrays.asList("ADMIN_OTHER")));
//        when(permissionService.findOne(any())).thenReturn(permission);
//
//
//        String requestBody = "{\n" +
//                "\t\"id\": \"0eb945a4-0ee5-4527-8294-e3ab905ee0f7\",\n" +
//                "\t\"type\": \"verify_permission\",\n" +
//                "\t\"ttid\": \"1234567890123\",\n" +
//                "\t\"origin\": \"sunseries_access_control_client\",\n" +
//                "\t\"datetime\": \"2016-09-13T08:19:58Z\",\n" +
//                "\t\"event_data\": {\n" +
//                "\t\t\"email\": \"pea.chiwa@sunseries.travel\",\n" +
//                "\t\t\"resource\": \"admin\",\n" +
//                "\t\t\"role\": \"ADMIN_SYSTEM\"\n" +
//                "\t}\n" +
//                "}";
//
//        EventNotification eventNotification = new Gson().fromJson(requestBody, EventNotification.class);
//        eventNotification = verifyPermission.performVerifyPermission(eventNotification);
//
//        System.out.println(eventNotification);
//        Assert.assertEquals("User denies.", eventNotification.getEventData().get("status"));
//
//    }
//
//    @Test
//    public void testVerifyPermissionNotFound() {
//        VerifyPermission verifyPermission = new VerifyPermission();
//
//        PermissionService permissionService = mock(PermissionService.class);
//        ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
//
//        verifyPermission.setPermissionService(permissionService);
//        verifyPermission.setProducerTemplate(producerTemplate);
//
//
//        String requestBody = "{\n" +
//                "\t\"id\": \"0eb945a4-0ee5-4527-8294-e3ab905ee0f7\",\n" +
//                "\t\"type\": \"verify_permission\",\n" +
//                "\t\"ttid\": \"1234567890123\",\n" +
//                "\t\"origin\": \"sunseries_access_control_client\",\n" +
//                "\t\"datetime\": \"2016-09-13T08:19:58Z\",\n" +
//                "\t\"event_data\": {\n" +
//                "\t\t\"email\": \"pea.chiwa@sunseries.travel\",\n" +
//                "\t\t\"resource\": \"admin\",\n" +
//                "\t\t\"role\": \"ADMIN_SYSTEM\"\n" +
//                "\t}\n" +
//                "}";
//
//        EventNotification eventNotification = new Gson().fromJson(requestBody, EventNotification.class);
//        eventNotification = verifyPermission.performVerifyPermission(eventNotification);
//
//        System.out.println(eventNotification);
//        Assert.assertEquals("User not found.", eventNotification.getEventData().get("status"));
//
//    }
//
//    @Test
//    public void testVerifyFail() {
//        VerifyPermission verifyPermission = new VerifyPermission();
//
//        PermissionService permissionService = mock(PermissionService.class);
//        ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
//
//        verifyPermission.setPermissionService(permissionService);
//        verifyPermission.setProducerTemplate(producerTemplate);
//
//
//        String requestBody = "{\n" +
//                "\t\"id\": \"0eb945a4-0ee5-4527-8294-e3ab905ee0f7\",\n" +
//                "\t\"type\": \"verify_permission\",\n" +
//                "\t\"ttid\": \"1234567890123\",\n" +
//                "\t\"origin\": \"sunseries_access_control_client\",\n" +
//                "\t\"datetime\": \"2016-09-13T08:19:58Z\",\n" +
//                "\t\"event_data\": {\n" +
//                "\t\t\"resource\": \"admin\",\n" +
//                "\t\t\"role\": \"ADMIN_SYSTEM\"\n" +
//                "\t}\n" +
//                "}";
//
//        EventNotification eventNotification = new Gson().fromJson(requestBody, EventNotification.class);
//        eventNotification = verifyPermission.performVerifyPermission(eventNotification);
//
//        System.out.println(eventNotification);
//        Assert.assertEquals("FAILED", eventNotification.getEventData().get("status"));
//
//    }
//
//
//}