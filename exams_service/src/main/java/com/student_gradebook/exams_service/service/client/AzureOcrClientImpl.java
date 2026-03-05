package com.student_gradebook.exams_service.service.client;

import com.student_gradebook.exams_service.controller.exceptions.FileProcessingException;
import com.student_gradebook.exams_service.records.OcrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AzureOcrClientImpl implements AzureOcrClient {
    private final WebClient webClient;
    @Override
    public String analyzePicture(byte[] imageBytes) {

        String operationId = webClient.post()
                .uri("/analyze")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(imageBytes)
                .exchangeToMono(response ->
                        Mono.just(
                                response.headers()
                                        .asHttpHeaders()
                                        .getFirst("apim-request-id")
                        )
                )
                .block();

        if (operationId == null) {
            throw new FileProcessingException("Image couldn't be processed by Azure Computer Vision.");
        }

        return operationId;
    }

    @Override
    public OcrResponse getAnalyzeResults(String id) {
        int attempts = 0;
        int maxAttempts = 15;

        while (attempts++ < maxAttempts) {

            OcrResponse response = webClient.get()
                    .uri("/analyzeResults/" + id)
                    .retrieve()
                    .bodyToMono(OcrResponse.class)
                    .block();

            if (response == null) {
                throw new FileProcessingException("Image couldn't be processed by Azure Computer Vision.");
            }

            if ("succeeded".equals(response.status())) {
                return response;
            }

            if ("failed".equals(response.status())) {
                throw new FileProcessingException("Image couldn't be processed by Azure Computer Vision.");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        throw new FileProcessingException("Image couldn't be processed by Azure Computer Vision.");
    }
}
