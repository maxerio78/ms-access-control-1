package sunseries.travel.access.control.camel.component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.apache.camel.ProducerTemplate;

import sunseries.travel.access.control.constant.Constant;
import sunseries.travel.constant.Kafka;
import sunseries.travel.constant.Origin;
import sunseries.travel.library.message.EventNotification;
import sunseries.travel.library.utility.DateUtils;

public abstract class MessageHandler {
    private ProducerTemplate producerTemplate;

    public MessageHandler(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    protected EventNotification createEventResponse(EventNotification eventNotification, Map<String, Object> eventData, String type) {
        eventNotification.setOrigin(Origin.MS_ACCESS_CONTROL);
        eventNotification.setType(type);
        eventNotification.setEventData(eventData);
        eventNotification.setDatetime(DateUtils.currentDateTimeWithUTC());
        return eventNotification;
    }

    protected void createGeneralException(Map<String, Object> eventData, String message) {
        eventData.put("code", Constant.PREFIX_ERROR_CODE + Constant.GENERAL_ERROR_CODE);
        eventData.put("status", "FAILURE");
        eventData.put("message", message);
    }

    protected void createNotFoundException(Map<String, Object> eventData) {
        eventData.put("code", Constant.PREFIX_ERROR_CODE + Constant.NOT_FOUND_ERROR_CODE);
        eventData.put("status", "FAILURE");
        eventData.put("message", "NOT_FOUND");
    }

    protected void createDuplicateResponse(Map<String, Object> eventData) {
        eventData.put("code", Constant.PREFIX_ERROR_CODE + Constant.DUPLICATED_ERROR_CODE);
        eventData.put("status", "FAILURE");
        eventData.put("message", "DUPLICATED");
    }

    protected void createSuccessResponse(Map<String, Object> eventData) {
        eventData.put("status", "SUCCESS");
    }

    protected void raiseEvent(Instant start, Instant end, EventNotification message) {
        message.setOrigin(Origin.MS_ACCESS_CONTROL);
        message.setProcessingTime(ChronoUnit.MILLIS.between(start, end));
        producerTemplate.sendBody(Kafka.PRODUCER, message.toJSONString());
    }

}
