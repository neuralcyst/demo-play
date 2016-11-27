package actors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collections;
import java.util.List;

@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME, property = "event")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessengerProtocol.AddMessageEvent.class, name = "add"),
        @JsonSubTypes.Type(value = MessengerProtocol.GetAllMessagesEvent.class, name = "getAll"),
        @JsonSubTypes.Type(value = MessengerProtocol.MessagesNotificationEvent.class, name = "notification")
})
public abstract class MessengerProtocol {

    public static class AddMessageEvent extends MessengerProtocol {
        public final String user;
        public final String message;
        @JsonCreator
        public AddMessageEvent(@JsonProperty("user") String user,
                               @JsonProperty("message") String message) {
            this.user = user;
            this.message = message;
        }
    }

    public static class MessagesNotificationEvent extends MessengerProtocol {
        public final List<Message> messages;
        @JsonCreator
        public MessagesNotificationEvent(@JsonProperty("messages") List<Message> messages) {
            this.messages = Collections.unmodifiableList(messages);
        }
    }

    public static class GetAllMessagesEvent extends MessengerProtocol {
        @JsonCreator
        public GetAllMessagesEvent() {
        }
    }

    public static class Message {
        public final String user;
        public final String message;
        @JsonCreator
        public Message(@JsonProperty("user") String user,
                       @JsonProperty("message") String message) {
            this.user = user;
            this.message = message;
        }
    }

}
