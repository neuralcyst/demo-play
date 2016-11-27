package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator.Publish;
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe;
import com.fasterxml.jackson.databind.JsonNode;
import models.MessageEntity;
import play.libs.Json;

import java.util.List;
import java.util.stream.Collectors;

import static actors.MessengerProtocol.*;

public class MessengerActor extends UntypedActor {

    private static final String CHAT_TOPIC_NAME = "chat_topic";

    private final ActorRef upstream;
    private final ActorRef mediator;

    public static Props props(ActorRef upstream) {
        return Props.create(MessengerActor.class, () -> new MessengerActor(upstream))
                .withDispatcher("actor.messenger-dispatcher");
    }

    private MessengerActor(ActorRef upstream) {
        this.upstream = upstream;
        this.mediator = DistributedPubSub.get(getContext().system()).mediator();
    }

    @Override
    public void preStart() throws Exception {
        mediator.tell(new Subscribe(CHAT_TOPIC_NAME, self()), self());
        super.preStart();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof JsonNode) {
            MessengerProtocol request = Json.fromJson((JsonNode) message, MessengerProtocol.class);
            if (request instanceof AddMessageEvent) {
                process((AddMessageEvent) request);
            } else if (request instanceof GetAllMessagesEvent) {
                process((GetAllMessagesEvent) request);
            }
        } else if (message instanceof MessagesNotificationEvent) {
            process((MessagesNotificationEvent) message);
        } else {
            unhandled(message);
        }
    }

    private void process(AddMessageEvent messageEvent) {
        MessageEntity message = new MessageEntity(messageEvent.user, messageEvent.message);
        message.insert();

        MessagesNotificationEvent notificationEvent = createMessagesNotificationEvent();

        mediator.tell(new Publish(CHAT_TOPIC_NAME, notificationEvent), self());
    }

    private void process(GetAllMessagesEvent messageEvent) {
        MessagesNotificationEvent notificationEvent = createMessagesNotificationEvent();

        upstream.tell(Json.toJson(notificationEvent), self());
    }

    private void process(MessagesNotificationEvent messageEvent) {
        upstream.tell(Json.toJson(messageEvent), self());
    }

    private MessagesNotificationEvent createMessagesNotificationEvent() {
        List<MessageEntity> entities = MessageEntity.FIND
                .orderBy("id desc")
                .findPagedList(0, 5)
                .getList();

        List<Message> messagesDTO = entities.stream()
                .map(entity -> new Message(entity.getUser(), entity.getMessage()))
                .collect(Collectors.toList());

        return new MessagesNotificationEvent(messagesDTO);
    }

}
