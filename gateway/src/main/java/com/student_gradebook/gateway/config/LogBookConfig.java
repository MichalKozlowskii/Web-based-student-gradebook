package com.student_gradebook.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.QueryFilter;
import org.zalando.logbook.Sink;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.core.QueryFilters;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class LogBookConfig {
    @Bean
    public Logbook logbook() {

        QueryFilter queryFilter = QueryFilter.merge(
                QueryFilters.replaceQuery("oauth_token", "XXX"),
                QueryFilters.replaceQuery("oauth_verifier", "XXX")
        );

        HeaderFilter headerFilter = HeaderFilter.merge(
                HeaderFilters.replaceHeaders(
                        Set.of("cookie", "authorization", "set-cookie"), "XXX"
                ),
                headers -> {
                    List<String> locations = headers.get("location");
                    if (locations != null) {
                        headers.put(
                                "location",
                                locations.stream()
                                        .map(this::removeOauthTokenFromUrl)
                                        .collect(Collectors.toList())
                        );
                    }
                    return headers;
                }
        );


        Sink sink = new DefaultSink(new JsonHttpLogFormatter(), new DefaultHttpLogWriter());

        return Logbook.builder()
                .queryFilter(queryFilter)
                .headerFilter(headerFilter)
                .sink(sink)
                .condition(request -> !request.getPath().startsWith("/actuator"))
                .build();
    }

    private String removeOauthTokenFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String newQuery = Stream.of(uri.getQuery() == null ? new String[0] : uri.getQuery().split("&"))
                    .filter(param -> !param.startsWith("oauth_token="))
                    .collect(Collectors.joining("&"));

            return new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    newQuery.isEmpty() ? null : newQuery,
                    uri.getFragment()
            ).toString();

        } catch (URISyntaxException e) {
            // If URI parsing fails, fallback to original
            return url;
        }
    }
}