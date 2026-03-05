package com.student_gradebook.exams_service.service.client;

import com.student_gradebook.exams_service.records.OcrResponse;

public interface AzureOcrClient {
    String analyzePicture(byte[] imageBytes);
    OcrResponse getAnalyzeResults(String url);
}
