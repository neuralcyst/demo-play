package filters;

import akka.stream.Materializer;
import play.filters.gzip.GzipFilter;
import play.filters.gzip.GzipFilterConfig;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class CustomGzipFilter extends GzipFilter {
    @Inject
    public CustomGzipFilter(@Named("Custom") GzipFilterConfig config, Materializer mat) {
        super(config, mat);
    }
}
