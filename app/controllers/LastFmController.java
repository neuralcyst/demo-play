package controllers;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import services.ContextProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Singleton
public class LastFmController extends Controller {

    private static final String LAST_FM_URL = "http://ws.audioscrobbler.com/2.0";

    private final WSClient wsClient;
    private final ContextProvider contextProvider;

    @Inject
    public LastFmController(WSClient wsClient, ContextProvider contextProvider) {
        this.wsClient = wsClient;
        this.contextProvider = contextProvider;
    }

    public CompletionStage<Result> similarArtists(String artist) {
        WSRequest request = createGetSimilarArtistRequest(artist);

        return request.get()
                .thenApply(WSResponse::asJson)
                .thenApply(node -> {
                    List<String> names = fromJsonToArtists(node).stream()
                            .map(a -> a.name)
                            .collect(Collectors.toList());
                    return ok(names.toString());
                });
    }

    /***
     * See also {@link LastFmController#biSimilarArtistsBlocking(String)} - example of how to do the same
     * thing with Blocking WS-calls in async environment}
     */
    public CompletionStage<Result> biSimilarArtists(String artist) {
        WSRequest request = createGetSimilarArtistRequest(artist);

        CompletionStage<List<String>> similarArtists = request.get()
                .thenApply(WSResponse::asJson)
                .thenApply(node -> fromJsonToArtists(node).stream()
                        .map(a -> a.name)
                        .collect(Collectors.toList()));

        CompletionStage<Set<String>> biSimilarArtists = similarArtists.thenComposeAsync(strings -> {
            List<CompletableFuture<WSResponse>> artistFutures = strings.stream()
                    .map(artistName -> createGetSimilarArtistRequest(artistName).get().toCompletableFuture())
                    .collect(Collectors.toList());

            CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(toFutureArray(artistFutures));

            return allOfFuture.thenApply(v -> artistFutures.stream()
                    .map(CompletableFuture::join)
                    .map(WSResponse::asJson)
                    .flatMap(similarNode -> fromJsonToArtists(similarNode).stream()
                            .map(a -> a.name))
                    .collect(Collectors.toSet()));
        });

        return biSimilarArtists.thenApply(artists -> ok(artists.toString()));
    }

    /***
     * See also {@link LastFmController#biSimilarArtists(String)} - example of pure async request processing
     */
    public CompletionStage<Result> biSimilarArtistsBlocking(String artist) {
        return CompletableFuture.supplyAsync(() -> {
                    Set<String> result = new HashSet<>();

                    WSRequest request = createGetSimilarArtistRequest(artist);
                    WSResponse response = blockingGet(request);

                    List<Artist> similarArtists = fromJsonToArtists(response.asJson());
                    for (Artist similarArtist : similarArtists) {
                        WSRequest similarArtistRequest = createGetSimilarArtistRequest(similarArtist.name);
                        WSResponse similarArtistResponse = blockingGet(similarArtistRequest);
                        List<Artist> biSimilarArtists = fromJsonToArtists(similarArtistResponse.asJson());
                        for (Artist biSimilarArtist : biSimilarArtists) {
                            result.add(biSimilarArtist.name);
                        }
                    }

                    return ok(result.toString());
                },
                contextProvider.getWsExecutor());
    }

    private WSResponse blockingGet(WSRequest request) {
        try {
            return request.get().toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<?>[] toFutureArray(List<CompletableFuture<WSResponse>> artistsFutures) {
        return artistsFutures.toArray(new CompletableFuture<?>[artistsFutures.size()]);
    }

    private WSRequest createGetSimilarArtistRequest(String artist) {
        return wsClient.url(LAST_FM_URL)
                .setQueryParameter("api_key", "169bb22719c920b255ccca73b0d83125")
                .setQueryParameter("method", "artist.getSimilar")
                .setQueryParameter("artist", artist)
                .setQueryParameter("format", "json")
                .setQueryParameter("limit", "5");
    }

    private List<Artist> fromJsonToArtists(JsonNode node) {
        return Json.fromJson(node.get("similarartists"), SimilarArtists.class).artists;
    }

    public static class SimilarArtists {
        final List<Artist> artists;

        @JsonCreator
        public SimilarArtists(@JsonProperty("artist") List<Artist> artists) {
            this.artists = artists;
        }
    }

    public static class Artist {
        final String name;
        final String url;

        @JsonCreator
        public Artist(@JsonProperty("name") String name, @JsonProperty("url") String url) {
            this.name = name;
            this.url = url;
        }
    }


}
