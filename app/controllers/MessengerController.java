package controllers;

import actors.MessengerActor;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.LegacyWebSocket;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.messenger.chat;

import javax.inject.Singleton;

@Singleton
public class MessengerController extends Controller {

    public Result index() {
        return ok(chat.render("Messenger Demo"));
    }

    public LegacyWebSocket<JsonNode> stream() {
        return WebSocket.withActor(upstream -> MessengerActor.props(upstream));
    }

}
