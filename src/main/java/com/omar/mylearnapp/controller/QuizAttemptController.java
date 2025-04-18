package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.dto.QuizAttemptDTO;
import com.omar.mylearnapp.model.QuizAttempt;
import com.omar.mylearnapp.model.Response;
import com.omar.mylearnapp.service.QuizAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startQuizAttempt(
            @RequestParam String clerkId,
            @RequestParam Long quizId) {

        try {
            QuizAttempt attempt = quizAttemptService.startQuizAttempt(clerkId, quizId);

            Map<String, Object> response = Map.of(
                    "attemptId", attempt.getId(),
                    "startTime", attempt.getStartTime().toString(),
                    "totalQuestions", attempt.getTotalQuestions()
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<?> submitQuizAttempt(
            @PathVariable Long attemptId,
            @RequestBody Map<Long, Long> responses) {

        try {
            QuizAttempt attempt = quizAttemptService.submitQuizAttempt(attemptId, responses);

            Map<String, Object> result = Map.of(
                    "attemptId", attempt.getId(),
                    "score", attempt.getScore(),
                    "totalQuestions", attempt.getTotalQuestions(),
                    "timeTakenSeconds", attempt.getTimeTakenSeconds(),
                    "correctAnswers", attempt.getScore()
            );

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{clerkId}")
    public ResponseEntity<List<QuizAttemptDTO>> getUserAttempts(@PathVariable String clerkId) {
        List<QuizAttempt> attempts = quizAttemptService.getUserAttempts(clerkId);
        List<QuizAttemptDTO> dtos = attempts.stream()
                .map(QuizAttemptDTO::fromQuizAttempt)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{clerkId}/recent")
    public ResponseEntity<List<QuizAttemptDTO>> getRecentUserAttempts(@PathVariable String clerkId) {
        List<QuizAttempt> attempts = quizAttemptService.getRecentUserAttempts(clerkId);
        List<QuizAttemptDTO> dtos = attempts.stream()
                .map(QuizAttemptDTO::fromQuizAttempt)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptDTO>> getQuizAttempts(@PathVariable Long quizId) {
        List<QuizAttempt> attempts = quizAttemptService.getQuizAttempts(quizId);
        List<QuizAttemptDTO> dtos = attempts.stream()
                .map(QuizAttemptDTO::fromQuizAttempt)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptDTO>> getUserAttemptsForQuiz(
            @PathVariable Long userId,
            @PathVariable Long quizId) {
        List<QuizAttempt> attempts = quizAttemptService.getUserAttemptsForQuiz(userId, quizId);
        List<QuizAttemptDTO> dtos = attempts.stream()
                .map(QuizAttemptDTO::fromQuizAttempt)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{attemptId}")
    public ResponseEntity<?> getAttemptById(@PathVariable Long attemptId) {
        Optional<QuizAttempt> attemptOpt = quizAttemptService.getAttemptById(attemptId);
        if (attemptOpt.isPresent()) {
            QuizAttemptDTO dto = QuizAttemptDTO.fromQuizAttempt(attemptOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Quiz attempt not found"));
        }
    }

    @GetMapping("/{attemptId}/responses")
    public ResponseEntity<List<Response>> getResponsesForAttempt(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizAttemptService.getResponsesForAttempt(attemptId));
    }

    @GetMapping("/{attemptId}/questions/{questionId}/response")
    public ResponseEntity<?> getResponseForQuestion(
            @PathVariable Long attemptId,
            @PathVariable Long questionId) {

        Response response = quizAttemptService.getResponseForQuestion(attemptId, questionId);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Response not found"));
        }
    }

    @DeleteMapping("/{attemptId}")
    public ResponseEntity<?> deleteQuizAttempt(@PathVariable Long attemptId) {
        try {
            quizAttemptService.deleteQuizAttempt(attemptId);
            return ResponseEntity.ok(Map.of("message", "Quiz attempt deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}