package com.student_gradebook.auth_server.service;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.student_gradebook.auth_server.records.groups.GroupsResponse;
import com.student_gradebook.auth_server.records.TermResponse;
import com.student_gradebook.auth_server.records.UserDetailsResponse;
import com.student_gradebook.auth_server.records.groups.ParticipantRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsosClientImpl implements UsosClient {
    private final WebClient webClient;

    @Override
    public UserDetailsResponse getUserDetails(OAuth1AccessToken accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/users/user")
                        .queryParam("fields", "id|first_name|last_name|student_status|staff_status")
                        .build())
                .header("X-User-Token", accessToken.getToken())
                .header("X-User-Secret", accessToken.getTokenSecret())
                .retrieve()
                .bodyToMono(UserDetailsResponse.class)
                .block();
    }

    @Override
    public List<TermResponse> getActiveTerm(OAuth1AccessToken accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/terms/search")
                        .queryParam("min_finish_date", LocalDate.now().toString())
                        .queryParam("max_start_date", LocalDate.now().toString())
                        .queryParam("query", "semestr")
                        .build())
                .header("X-User-Token", accessToken.getToken())
                .header("X-User-Secret", accessToken.getTokenSecret())
                .retrieve()
                .bodyToFlux(TermResponse.class)
                .collectList()
                .block();
    }

    @Override
    public GroupsResponse getGroups(OAuth1AccessToken accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/groups/lecturer")
                        .queryParam("fields",
                                "course_unit_id|group_number|course_name|participants")
                        .build())
                .header("X-User-Token", accessToken.getToken())
                .header("X-User-Secret", accessToken.getTokenSecret())
                .retrieve()
                .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .doOnNext(body -> System.err.println("USOS error response: " + body))
                                .flatMap(body -> Mono.error(new RuntimeException("USOS API error " + response.statusCode() + ": " + body)))
                )
                .bodyToMono(GroupsResponse.class)
                .block();
    }

    @Override
    public ParticipantRecord getUserInfo(OAuth1AccessToken accessToken, String userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/users/user")
                        .queryParam("user_id", userId)
                        .queryParam("fields",
                                "id|first_name|last_name|student_number")
                        .build())
                .header("X-User-Token", accessToken.getToken())
                .header("X-User-Secret", accessToken.getTokenSecret())
                .retrieve()
                .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .doOnNext(body -> System.err.println("USOS error response: " + body))
                                .flatMap(body -> Mono.error(new RuntimeException("USOS API error " + response.statusCode() + ": " + body)))
                )
                .bodyToMono(ParticipantRecord.class)
                .block();
    }
}
