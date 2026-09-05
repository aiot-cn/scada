package org.aiot.lang;

import org.aiot.model.enums.EventEnum;

public class NotifyEvent {

    private final EventEnum eventType;
    private final Object data; //当前的数据

    private Object data2;

    public NotifyEvent(EventEnum eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public NotifyEvent(EventEnum eventType, Object data,Object data2) {
        this(eventType,data);
        this.data2 = data2;
    }

    public EventEnum getEventType() {
        return eventType;
    }

    /**
     * 当前的数据，一定存在
     */
    public Object getData() {
        return data;
    }

    /**
     * 可能是之前的数据，可能不存在
     */
    public <T> T getData2() {
        return (T)data2;
    }

}
