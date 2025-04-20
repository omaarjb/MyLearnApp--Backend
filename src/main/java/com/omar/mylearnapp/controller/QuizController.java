package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.dto.QuizDTO;
import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.model.User;
import com.omar.mylearnapp.model.response.QuizResponse;
import com.omar.mylearnapp.service.GeminiService;
import com.omar.mylearnapp.service.QuizService;
import com.omar.mylearnapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @Autowired
    private GeminiService geminiService;

    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        List<QuizDTO> response = quizzes.stream()
                .map(QuizDTO::fromQuiz)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete/professor/{professorId}")
    public ResponseEntity<?>
    createCompleteQuizWithProfessor(
            @PathVariable String professorId,
            @RequestBody Quiz quiz) {

        Optional<User> professor = userService.findByClerkId(professorId);
        if (!professor.isPresent()) {
            return ResponseEntity.status(404).body(Map.of("error", "Professor not found"));
        }

        if (!"professeur".equalsIgnoreCase(professor.get().getRole())) {
            return ResponseEntity.status(403).body(Map.of("error", "User is not a professor"));
        }

        quiz.setProfessor(professor.get());
        Quiz createdQuiz = quizService.createCompleteQuiz(quiz);
        return new ResponseEntity<>(QuizResponse.fromQuiz(createdQuiz), HttpStatus.CREATED);
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<QuizDTO>>
    getQuizzesByProfessor(@PathVariable Long professorId) {
        List<Quiz> quizzes = quizService.getQuizzesByProfessor(professorId);
        List<QuizDTO> response = quizzes.stream()
                .map(QuizDTO::fromQuiz)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody Quiz quiz){
        Quiz createdQuiz = quizService.createQuiz(quiz);
        return new ResponseEntity<>(QuizResponse.fromQuiz(createdQuiz), HttpStatus.CREATED);
    }

    @PostMapping("/complete")
    public ResponseEntity<QuizResponse> createCompleteQuiz(@RequestBody Quiz quiz){
        Quiz createdQuiz = quizService.createCompleteQuiz(quiz);
        return new ResponseEntity<>(QuizResponse.fromQuiz(createdQuiz), HttpStatus.CREATED);
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByTopic(@PathVariable Long topicId) {
        List<Quiz> quizzes = quizService.getQuizzesByTopic(topicId);
        List<QuizDTO> response = quizzes.stream()
                .map(QuizDTO::fromQuiz)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByDifficulty(@PathVariable String difficulty) {
        List<Quiz> quizzes = quizService.getQuizzesByDifficulty(difficulty);
        List<QuizDTO> response = quizzes.stream()
                .map(QuizDTO::fromQuiz)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @RequestBody Quiz quiz) {
        if (!quizService.getQuizById(id).isPresent()) {
            return ResponseEntity.status(404).body(Map.of("error", "Quiz not found"));
        }

        Quiz updatedQuiz = quizService.updateQuiz(id, quiz);
        return ResponseEntity.ok(QuizResponse.fromQuiz(updatedQuiz));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        if (!quizService.getQuizById(id).isPresent()) {
            return ResponseEntity.status(404).body(Map.of("error", "Quiz not found"));
        }

        quizService.deleteQuiz(id);
        return ResponseEntity.ok(Map.of("message", "Quiz deleted successfully"));
    }


    @PostMapping("/generate-with-ai")
    public ResponseEntity<?> generateQuizWithAI(@RequestBody Map<String, Object> request, @RequestParam String professorId) {
        try {
            // Extract parameters from request
            String sourceType = (String) request.get("sourceType");
            String content = (String) request.get("content");
            int numQuestions = (int) request.get("numQuestions");
            String difficulty = (String) request.get("difficulty");
            String category = (String) request.getOrDefault("category", "Programmation");

            // Validate professor
            Optional<User> professor = userService.findByClerkId(professorId);
            if (!professor.isPresent()) {
                return ResponseEntity.status(404).body(Map.of("error", "Professor not found"));
            }

            if (!"professeur".equalsIgnoreCase(professor.get().getRole())) {
                return ResponseEntity.status(403).body(Map.of("error", "User is not a professor"));
            }

            // Generate quiz using Gemini
            Quiz generatedQuiz = geminiService.generateQuiz(sourceType, content, numQuestions, difficulty, category);

            // Set professor
            generatedQuiz.setProfessor(professor.get());

            // Return the generated quiz (without saving it yet)
            return ResponseEntity.ok(QuizDTO.fromQuiz(generatedQuiz));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to generate quiz",
                    "message", e.getMessage()
            ));
        }
    }
}
