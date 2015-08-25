package org.jainteto.itp.core.transport;

import java.io.Serializable;

public class Message implements Serializable {

    public static final int STATUS_NO            = 0;
    public static final int STATUS_MESSAGE       = 1;
    public static final int STATUS_ERROR         = 2;

    private static final Message NO_MESSAGE_Object = new Message("", STATUS_NO);

    private final String message;
    private final int    status;

    public static Message noMessage() { return NO_MESSAGE_Object; }

    public static Message message(String message) {
        return new Message(message, STATUS_MESSAGE);
    }

    public static Message error(String message) {
        return new Message(message, STATUS_ERROR);
    }

    private Message(String message, int status) {
        this.message = message == null ? "" : message;
        this.status  = status;
    }

    public String getMessage() { return message; }

    public int getMessageStatus() { return status; }

    public String toString() { return message; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Message)) return false;
        Message message = (Message)obj;
        return this.status == message.getMessageStatus()
                && this.message.equals(message.getMessage());
    }

    @Override
    public int hashCode() {
        return 31 *  message.hashCode() + status;
    }

}
