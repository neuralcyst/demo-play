package controllers;

import akka.actor.ActorSystem;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Singleton
public class SimpleController extends Controller {

    private final HttpExecutionContext context;
    private final ActorSystem actorSystem;
    private final ExecutionContextExecutor executor;

    @Inject
    public SimpleController(HttpExecutionContext context,
                            ActorSystem actorSystem,
                            ExecutionContextExecutor executor) {
        this.context = context;
        this.actorSystem = actorSystem;
        this.executor = executor;
    }


    public Result selfSync(Integer arg) {
        return ok("<h1>int: " + request().getQueryString("arg") + "</h1>").as("text/html");
    }

    public Result sessioned() {
        String storedValue = session("STORED_VALUE");
        if (storedValue == null) {
            session("STORED_VALUE", "1");
            return ok("Stored!");
        } else if (storedValue.equals("1")) {
            session("STORED_VALUE", "2");
            return ok("Found value: 1");
        } else {
            session().remove("STORED_VALUE");
            return ok("Found value: " + storedValue);
        }
    }

    public CompletionStage<Result> selfAsync(String arg) {
        return CompletableFuture.supplyAsync(
                () -> {
                    response().setHeader("CUSTOM", "777");
                    return ok(arg);
                },
                context.current());
    }


    public CompletionStage<Result> selfAsyncErr() {
        return CompletableFuture.supplyAsync(() -> {
            Supplier s = () -> {throw new RuntimeException("Oooops! ^^");};
            return ok(s.get().toString());
        }).exceptionally(throwable -> badRequest(throwable.getMessage()));
    }

    public  CompletionStage<Result> selfDelayed(String arg, String time) {
        CompletableFuture<Result> future = new CompletableFuture<>();
        actorSystem.scheduler().scheduleOnce(
                Duration.create(new Long(time), TimeUnit.SECONDS),
                () -> future.complete(ok(arg)),
                executor);
        return future;
    }

}
