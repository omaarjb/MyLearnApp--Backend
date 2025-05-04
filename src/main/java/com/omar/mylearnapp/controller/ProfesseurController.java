package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.dto.QuizDTO;
import com.omar.mylearnapp.model.Question;
import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.model.User;
import com.omar.mylearnapp.model.response.QuizResponse;
import com.omar.mylearnapp.service.QuizService;
import com.omar.mylearnapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professeur")
public class ProfesseurController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    /**
     * Récupère tous les quiz créés par le professeur
     */
    @GetMapping("/{clerkId}/quizzes")
    public ResponseEntity<?> getAllProfesseurQuizzes(@PathVariable String clerkId) {
        Optional<User> professeurOpt = userService.findByClerkId(clerkId);

        if (professeurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professeur non trouvé");
        }

        User professeur = professeurOpt.get();

        // Vérifier que l'utilisateur est un professeur
        if (!"professeur".equalsIgnoreCase(professeur.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("L'utilisateur n'est pas un professeur");
        }

        List<Quiz> quizzes = quizService.getQuizzesByProfessor(professeur.getId());

        // Ensure each quiz has its topic properly loaded before converting to DTO
        List<QuizDTO> quizDTOs = quizzes.stream()
                .map(quiz -> {
                    // Make sure topic is not null in the DTO if it exists in the entity
                    return QuizDTO.fromQuiz(quiz);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(quizDTOs);
    }

    /**
     * Crée un nouveau quiz
     */
    @PostMapping("/{clerkId}/quizzes")
    public ResponseEntity<?> createQuiz(@PathVariable String clerkId, @RequestBody Quiz quiz) {
        Optional<User> professeurOpt = userService.findByClerkId(clerkId);

        if (professeurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professeur non trouvé");
        }

        User professeur = professeurOpt.get();

        // Vérifier que l'utilisateur est un professeur
        if (!"professeur".equalsIgnoreCase(professeur.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("L'utilisateur n'est pas un professeur");
        }

        try {
            Quiz createdQuiz = quizService.createQuizForProfessor(quiz, professeur.getId());
            // Use QuizDTO instead of QuizResponse to include topic
            return ResponseEntity.status(HttpStatus.CREATED).body(QuizDTO.fromQuiz(createdQuiz));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Met à jour un quiz existant
     */
    @PutMapping("/{clerkId}/quizzes/{quizId}")
    public ResponseEntity<?> updateQuiz(
            @PathVariable String clerkId,
            @PathVariable Long quizId,
            @RequestBody Quiz quiz) {

        Optional<User> professeurOpt = userService.findByClerkId(clerkId);

        if (professeurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professeur non trouvé");
        }

        User professeur = professeurOpt.get();

        try {
            Quiz updatedQuiz = quizService.updateProfessorQuiz(quizId, quiz, professeur.getId());
            // Use QuizDTO instead of QuizResponse to include topic
            return ResponseEntity.ok(QuizDTO.fromQuiz(updatedQuiz));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Supprime un quiz
     */
    @DeleteMapping("/{clerkId}/quizzes/{quizId}")
    public ResponseEntity<?> deleteQuiz(
            @PathVariable String clerkId,
            @PathVariable Long quizId) {

        Optional<User> professeurOpt = userService.findByClerkId(clerkId);

        if (professeurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professeur non trouvé");
        }

        User professeur = professeurOpt.get();

        try {
            quizService.deleteProfessorQuiz(quizId, professeur.getId());
            return ResponseEntity.ok("Quiz supprimé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Ajoute une question à un quiz
     */
    @PostMapping("/{clerkId}/quizzes/{quizId}/questions")
    public ResponseEntity<?> addQuestion(
            @PathVariable String clerkId,
            @PathVariable Long quizId,
            @RequestBody Question question) {

        Optional<User> professeurOpt = userService.findByClerkId(clerkId);

        if (professeurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professeur non trouvé");
        }

        User professeur = professeurOpt.get();

        try {
            Question createdQuestion = quizService.addQuestionToQuiz(quizId, question, professeur.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Met à jour une question
     */
    @PutMapping("/{clerkId}/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable String clerkId,
            @PathVariable Long questionId,
            @RequestBody Question question) {

        Optional<User> professeurOpt = userService.findByClerkId(clerkId);

        if (professeurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professeur non trouvé");
        }

        User professeur = professeurOpt.get();

        try {
            Question updatedQuestion = quizService.updateQuestion(questionId, question, professeur.getId());
            return ResponseEntity.ok(updatedQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Supprime une question
     */
    @DeleteMapping("/{clerkId}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable String clerkId,
            @PathVariable Long questionId) {

        Optional<User> professeurOpt = userService.findByClerkId(clerkId);

        if (professeurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professeur non trouvé");
        }

        User professeur = professeurOpt.get();

        try {
            quizService.deleteQuestion(questionId, professeur.getId());
            return ResponseEntity.ok("Question supprimée avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Récupère un quiz spécifique par ID (si appartenant au professeur)
     */
    @GetMapping("/{clerkId}/quizzes/{quizId}")
    public ResponseEntity<?> getQuizById(
            @PathVariable String clerkId,
            @PathVariable Long quizId) {

        Optional<User> professeurOpt = userService.findByClerkId(clerkId);

        if (professeurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professeur non trouvé");
        }

        User professeur = professeurOpt.get();
        Optional<Quiz> quizOpt = quizService.getQuizById(quizId);

        if (quizOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz non trouvé");
        }

        Quiz quiz = quizOpt.get();

        // Vérifier si le professeur est le propriétaire
        if (quiz.getProfessor() == null || !quiz.getProfessor().getId().equals(professeur.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ce quiz ne vous appartient pas");
        }

        // Use QuizDTO to avoid infinite recursion
        return ResponseEntity.ok(QuizDTO.fromQuiz(quiz));
    }
}