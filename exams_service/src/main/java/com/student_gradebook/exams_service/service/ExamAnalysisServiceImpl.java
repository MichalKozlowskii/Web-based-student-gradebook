package com.student_gradebook.exams_service.service;

import com.student_gradebook.exams_service.controller.exceptions.EntityNotFoundException;
import com.student_gradebook.exams_service.controller.exceptions.FileProcessingException;
import com.student_gradebook.exams_service.controller.exceptions.ImageGenerationException;
import com.student_gradebook.exams_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.exams_service.dto.ScanResultDto;
import com.student_gradebook.exams_service.entity.Exam;
import com.student_gradebook.exams_service.records.OcrResponse;
import com.student_gradebook.exams_service.records.Word;
import com.student_gradebook.exams_service.repository.ExamRepository;
import com.student_gradebook.exams_service.service.client.AzureOcrClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamAnalysisServiceImpl implements ExamAnalysisService {
    private final ExamRepository examRepository;
    private final AzureOcrClientImpl azureOcrClient;
    private final ImageProcessingService imageProcessingService;

    private final String ANSWER_1PT = "A";
    private final String ANSWER_05PT = "B";

    @Override
    public byte[] addTable(UUID examId, String lecturerId) {
        Exam exam = examRepository.findById(examId).orElseThrow(
                () -> new EntityNotFoundException("Exam not found!")
        );

        if (!exam.getLecturerId().equals(lecturerId))
            throw new UnAuthorizedActionException("You are not permitted!");

        int boxWidth = 20;
        int boxHeight = 15;
        int numberWidth = 20;
        int indexFieldsWidth = 65;
        int framePadding = 5;

        int tableHeight = exam.getNumberOfTasks() * boxHeight + 40;
        int frameWidth = indexFieldsWidth + (2 * framePadding);
        int imgWidth = frameWidth + 40;
        int imgHeight = tableHeight + 60;

        BufferedImage image = new BufferedImage(
                imgWidth,
                imgHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = imgWidth / 2;
        int frameX = centerX - (frameWidth / 2);
        int frameY = 20;
        int tableX = centerX - ((numberWidth + boxWidth) / 2);
        int startY = frameY + 30;

        g.setColor(Color.BLACK);
        g.drawRect(frameX, frameY, frameWidth, tableHeight);

        g.setFont(new Font("Helvetica", Font.PLAIN, 8));
        g.drawString("__ __ __ __ __ __",
                centerX - (indexFieldsWidth / 2) + 4,
                frameY + 20);

        // Add the "▲ ■" text above the frame
        g.setFont(new Font("Helvetica", Font.PLAIN, 16));
        g.drawString("▲ ■",
                centerX - 14, // adjust to center approximately
                frameY - 5); // a little above the top of the frame

        g.setFont(new Font("Helvetica", Font.PLAIN, 12));
        for (int i = 1; i <= exam.getNumberOfTasks(); i++) {
            int rowY = startY + (i - 1) * boxHeight;

            g.drawRect(tableX, rowY, numberWidth, boxHeight);
            g.drawRect(tableX + numberWidth, rowY, boxWidth, boxHeight);

            g.drawString(i + ")", tableX + 5, rowY + 12);
        }

        g.dispose();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ImageGenerationException("Image couldn't be generated.");
        }
    }


    @Override
    public ScanResultDto analyzeExam(MultipartFile image, UUID examId, String lecturerId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found!"));
        if (!exam.getLecturerId().equals(lecturerId))
            throw new UnAuthorizedActionException("You are not permitted!");

        byte[] imageBytes;
        try {
            imageBytes = imageProcessingService.process(image.getBytes());
        } catch (IOException e) {
            throw new FileProcessingException("Image couldn't be processed!");
        }

        String analyzeId = azureOcrClient.analyzePicture(imageBytes);
        OcrResponse ocrResponse = azureOcrClient.getAnalyzeResults(analyzeId);

        return analyze(exam, ocrResponse);
    }

    private ScanResultDto analyze(Exam exam, OcrResponse ocrResponse) {
        String studentNumber = null;
        Map<Integer, AnswerCandidate> answers = new HashMap<>();

        List<Word> allWords = ocrResponse.analyzeResult().readResults()
                .stream()
                .flatMap(rr -> rr.lines().stream())
                .flatMap(line -> line.words().stream())
                .toList();

        // pattern for finding numerals with optional answer: "1)1A", "2)B", etc
        Pattern questionPattern = Pattern.compile("(\\d{1,2})\\)(.*)");

        for (Word word : allWords) {
            String text = word.text();

            // looking for index, if not found go right to the next word
            if (studentNumber == null) {
                if (text.matches("\\d{6}")) {
                    studentNumber = text;
                }
                continue;
            }

            // looking for numeration
            Matcher matcher = questionPattern.matcher(text);
            if (matcher.matches()) {
                int questionNum = Integer.parseInt(matcher.group(1));
                String restOfText = matcher.group(2).trim();

                AnswerCandidate candidate = new AnswerCandidate(
                        getWordX(word),
                        getWordY(word),
                        extractGradeFromText(restOfText) // extract answer if its in the same word as numeration
                );

                answers.put(questionNum, candidate);
                continue;
            }

            // Model might read 'B' as 8
            String normalized = text.length() <= 3 ? text.replace("8", "B") : text;

            // find answers that were split to other words
            if (isGrade(normalized)) {
                assignGradeToNearestQuestion(answers, word, normalized);
            }
        }

        if (studentNumber == null)
            throw new FileProcessingException("Student index not found");

        validateAllQuestionsPresent(answers, exam.getNumberOfTasks());

        return computeResult(exam, studentNumber, answers);
    }

    private static class AnswerCandidate {
        int x, y;
        Double grade; // null if not assigned

        AnswerCandidate(int x, int y, Double grade) {
            this.x = x;
            this.y = y;
            this.grade = grade;
        }
    }

    private Double extractGradeFromText(String text) {
        if (text.isEmpty()) return null;

        // Usuń potencjalną "1" z linii tabelki: "1A" -> "A"
        String cleaned = text.replaceAll("[^AB]", "");

        if (cleaned.contains(ANSWER_05PT)) return 0.5;
        if (cleaned.contains(ANSWER_1PT)) return 1.0;

        return null;
    }

    private void assignGradeToNearestQuestion(Map<Integer, AnswerCandidate> answers,
                                              Word gradeWord, String gradeText) {
        int gradeX = getWordX(gradeWord);
        int gradeY = getWordY(gradeWord);
        Double gradeValue = extractGradeFromText(gradeText);

        if (gradeValue == null) return;

        answers.values().stream()
                .filter(candidate -> {
                    int distanceX = gradeX - candidate.x;
                    int distanceY = Math.abs(gradeY - candidate.y);
                    return distanceX >= 20 && distanceX <= 250 && distanceY <= 20;
                })
                .min(Comparator.comparingInt(c -> Math.abs(gradeY - c.y)))
                .ifPresent(candidate -> {
                    if (candidate.grade == null) {
                        candidate.grade = gradeValue;
                    }
                });
    }

    private void validateAllQuestionsPresent(Map<Integer, AnswerCandidate> answers,
                                             int expectedCount) {
        for (int i = 1; i <= expectedCount; i++) {
            if (!answers.containsKey(i)) {
                throw new FileProcessingException("Missing question: " + i);
            }
        }
    }

    private ScanResultDto computeResult(Exam exam, String studentNumber,
                                        Map<Integer, AnswerCandidate> answers) {
        double totalScore = 0;
        TreeMap<Integer, Double> result = new TreeMap<>();

        for (int i = 1; i <= exam.getNumberOfTasks(); i++) {
            AnswerCandidate candidate = answers.get(i);
            double score = candidate.grade != null ? candidate.grade : 0.0;
            result.put(i, score);
            totalScore += score;
        }

        int resultPercent = new BigDecimal(totalScore * 100.0)
                .divide(new BigDecimal(exam.getNumberOfTasks()), 2, RoundingMode.HALF_UP)
                .intValue();

        return ScanResultDto.builder()
                .numberOfTasks(exam.getNumberOfTasks())
                .result(result)
                .totalScore(totalScore)
                .resultPercent(resultPercent)
                .grade(computeGrade(exam.getScope(), resultPercent))
                .studentNumber(studentNumber)
                .build();
    }
    private int getWordX(Word word) {
        // BoundingBox format: [x1, y1, x2, y2, x3, y3, x4, y4]
        // Return leftmost X coordinate (average of left edge points)
        List<Integer> bbox = word.boundingBox();
        return (bbox.get(0) + bbox.get(6)) / 2;
    }

    private int getWordY(Word word) {
        // BoundingBox format: [x1, y1, x2, y2, x3, y3, x4, y4]
        // Return topmost Y coordinate (average of top edge points)
        List<Integer> bbox = word.boundingBox();
        return (bbox.get(1) + bbox.get(3)) / 2;
    }

    private boolean isGrade(String text) {
        return (text.contains(ANSWER_05PT) || text.contains(ANSWER_1PT)) && text.length() <= 3;
    }

    private String computeGrade(TreeMap<Integer, Double> scope, double resultPercent) {
        int biggestThreshold = 0;
        for (Integer threshold : scope.keySet()) {
            if (resultPercent >= threshold) {
                biggestThreshold = threshold;
            }
        }

        String grade;
        if (biggestThreshold == 0) {
            grade = "2.0";
        } else {
            grade = scope.get(biggestThreshold).toString();
        }

        return grade;
    }
}