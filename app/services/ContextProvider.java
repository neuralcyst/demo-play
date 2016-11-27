package services;

import java.util.concurrent.Executor;

public interface ContextProvider {
    Executor getWsExecutor();
}
