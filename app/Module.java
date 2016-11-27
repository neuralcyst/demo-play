import com.google.inject.AbstractModule;
import java.time.Clock;

import com.google.inject.name.Names;
import play.filters.gzip.GzipFilter;
import play.filters.gzip.GzipFilterConfig;
import services.*;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(ContextProvider.class).to(ContextProviderImpl.class);
        bind(GzipFilterConfig.class)
                .annotatedWith(Names.named("Custom"))
                .toInstance(new GzipFilterConfig()
                        .withShouldGzip((requestHeader, result) ->
                                result.body().contentType().orElse("").startsWith("text/html")
                        )
                );
    }

}
