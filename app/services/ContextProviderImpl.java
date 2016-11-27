package services;

import akka.actor.ActorSystem;
import play.libs.concurrent.HttpExecution;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Executor;

@Singleton
public class ContextProviderImpl implements ContextProvider {

    private final Executor wsExecutor;

    @Inject
    public ContextProviderImpl(ActorSystem actorSystem) {
        this.wsExecutor = actorSystem.dispatchers().lookup("contexts.ws-context");
    }

    @Override
    public Executor getWsExecutor() {
        return wsExecutor;
    }
}
