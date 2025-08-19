//package sunseries.travel.access.control;
//
//
//import com.github.brainlag.nsq.NSQProducer;
//import org.junit.Test;
//import sunseries.travel.access.control.utility.DateUtils;
//
//import java.util.UUID;
//
//public class TestPermission {
//
//    @Test
//    public void shouldCreatePermissionSuccess() throws Exception {
//        NSQProducer producer = new NSQProducer().addAddress("dockerlb", 4150);
//        producer.start();
//        for (int i = 1; i <= 1; i++) {
//            String id = UUID.randomUUID().toString();
//            String nowAsISO = DateUtils.currentISODateWithUTC();
//            String body = "{" +
//                    "\"id\": \""+id+"\"," +
//                    "\"type\": \"create_permission\"," +
//                    "\"ttid\": \"1234567890123\"," +
//                    "\"origin\": \"sunseries_access_control_client\"," +
//                    "\"datetime\": \"" + nowAsISO + "\"," +
//                    "\"event_data\": {" +
//                    "\"email\": \"jack.poramet@sunseries.travel\"," +
//                    "\"resource\": \"admin\"," +
//                    "\"roles\": [\"ADMIN_SYSTEM\", \"ADMIN_HOTELIER\", \"ADMIN_AGENT\", \"MANAGE_BOOKING\"]}"+
//                    "}";
//
//            System.out.println("Done : " + body);
//            producer.produce("sunseries", body.getBytes());
//
//        }
//        producer.shutdown();
//    }
//
//    @Test
//    public void shouldUpdatePermissionSuccess() throws Exception {
//        NSQProducer producer = new NSQProducer().addAddress("dockerlb", 4150);
//        producer.start();
//        for (int i = 1; i <= 1; i++) {
//            String id = UUID.randomUUID().toString();
//            String nowAsISO = DateUtils.currentISODateWithUTC();
//            String body = "{" +
//                    "\"id\": \""+id+"\"," +
//                    "\"type\": \"update_permission\"," +
//                    "\"ttid\": \"1234567890123\"," +
//                    "\"origin\": \"sunseries_access_control_client\"," +
//                    "\"datetime\": \"" + nowAsISO + "\"," +
//                    "\"event_data\": {" +
//                    "\"email\": \"jack.poramet@sunseries.travel\"," +
//                    "\"resource\": \"admin\"," +
//                    "\"roles\": [\"ADMIN_SYSTEM\", \"ADMIN_HOTELIER\", \"ADMIN_AGENT\", \"MANAGE_BOOKING\"]}"+
//                    "}";
//
//            System.out.println("Done : " + body);
//            producer.produce("sunseries", body.getBytes());
//
//        }
//        producer.shutdown();
//    }
//
//    @Test
//    public void shouldDeletePermissionSuccess() throws Exception {
//        NSQProducer producer = new NSQProducer().addAddress("dockerlb", 4150);
//        producer.start();
//        for (int i = 1; i <= 1; i++) {
//            String id = UUID.randomUUID().toString();
//            String nowAsISO = DateUtils.currentISODateWithUTC();
//            String body = "{" +
//                    "\"id\": \""+id+"\"," +
//                    "\"type\": \"delete_permission\"," +
//                    "\"ttid\": \"1234567890123\"," +
//                    "\"origin\": \"sunseries_access_control_client\"," +
//                    "\"datetime\": \"" + nowAsISO + "\"," +
//                    "\"event_data\": {" +
//                    "\"email\": \"jack.poramet@sunseries.travel\"," +
//                    "\"resource\": \"admin\"," +
//                    "\"roles\": [\"ADMIN_SYSTEM\", \"ADMIN_HOTELIER\", \"ADMIN_AGENT\", \"MANAGE_BOOKING\"]}"+
//                    "}";
//
//            System.out.println("Done : " + body);
//            producer.produce("sunseries", body.getBytes());
//
//        }
//        producer.shutdown();
//    }
//
//    @Test
//    public void shouldGetPermissionSuccess() throws Exception {
//        NSQProducer producer = new NSQProducer().addAddress("dockerlb", 4150);
//        producer.start();
//        for (int i = 1; i <= 1; i++) {
//            String id = UUID.randomUUID().toString();
//            String nowAsISO = DateUtils.currentISODateWithUTC();
//            String body = "{" +
//                    "\"id\": \""+id+"\"," +
//                    "\"type\": \"get_permission\"," +
//                    "\"ttid\": \"1234567890123\"," +
//                    "\"origin\": \"sunseries_access_control_client\"," +
//                    "\"datetime\": \"" + nowAsISO + "\"," +
//                    "\"event_data\": {" +
//                    "\"email\": \"jack.poramet@sunseries.travel\"," +
//                    "\"resource\": \"admin\"}" +
//                    "}";
//
//            System.out.println("Done : " + body);
//            producer.produce("sunseries", body.getBytes());
//
//        }
//        producer.shutdown();
//    }
//
//}
