package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.*;
import com.omar.mylearnapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository; // Add this repository

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public List<Quiz> getQuizzesByTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + topicId));
        return quizRepository.findByTopicId(topic.getId());
    }

    public List<Quiz> getQuizzesByTopic(String topicName) {
        return quizRepository.findByTopic_Name(topicName);
    }

    public List<Quiz> getQuizzesByDifficulty(String difficulty) {
        return quizRepository.findByDifficulty(difficulty);
    }

    public List<Quiz> getQuizzesByCategory(String category) {
        return quizRepository.findByCategory(category);
    }


    public List<Quiz> getQuizzesByProfessor(Long professorId) {
        List<Quiz> quizzes = quizRepository.findByProfessorId(professorId);

        // Ensure topics are loaded for each quiz
        quizzes.forEach(quiz -> {
            if (quiz.getTopic() != null) {
                // Force loading the topic's properties to make sure they're available
                Topic topic = quiz.getTopic();
                topic.getName(); // This forces Hibernate to load the topic
                topic.getDescription();
            }
        });

        return quizzes;
    }

    @Transactional
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz createCompleteQuiz(Quiz quiz) {
        if (quiz.getTopic() != null && quiz.getTopic().getId() != null) {
            Topic topic = topicRepository.findById(quiz.getTopic().getId())
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + quiz.getTopic().getId()));
            quiz.setTopic(topic);
        }

        Quiz savedQuiz = quizRepository.save(quiz);

        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                question.setQuiz(savedQuiz);
                Question savedQuestion = questionRepository.save(question);

                if (question.getOptions() != null) {
                    for (Option option : question.getOptions()) {
                        option.setQuestion(savedQuestion);
                        optionRepository.save(option);
                    }
                }
            }
        }

        return quizRepository.findById(savedQuiz.getId()).orElse(savedQuiz);
    }

    @Transactional
    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        quiz.setDifficulty(quizDetails.getDifficulty());
        quiz.setIcon(quizDetails.getIcon());
        quiz.setColor(quizDetails.getColor());
        quiz.setCategory(quizDetails.getCategory());
        quiz.setTimeLimit(quizDetails.getTimeLimit());

        if (quizDetails.getTopic() != null && quizDetails.getTopic().getId() != null) {
            Topic topic = topicRepository.findById(quizDetails.getTopic().getId())
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + quizDetails.getTopic().getId()));
            quiz.setTopic(topic);
        }

        return quizRepository.save(quiz);
    }

    /**
     * Delete a quiz with proper handling of all dependencies
     */
    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        // Delete all quiz attempts and associated responses first
        deleteAllQuizAttempts(quiz.getId());

        // Delete all questions and their dependencies
        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                deleteQuestionWithDependencies(question);
            }
        }

        // Now safely delete the quiz
        quizRepository.delete(quiz);
    }

    /**
     * Helper method to delete all attempts for a quiz
     */
    @Transactional
    protected void deleteAllQuizAttempts(Long quizId) {
        // Find all quiz attempts for this quiz
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizId(quizId);

        for (QuizAttempt attempt : attempts) {
            // Delete all responses for this attempt
            List<Response> responses = responseRepository.findByQuizAttemptId(attempt.getId());
            for (Response response : responses) {
                responseRepository.delete(response);
            }

            // Delete the attempt itself
            quizAttemptRepository.delete(attempt);
        }
    }

    /**
     * Helper method to delete a question with all its dependencies
     */
    @Transactional
    protected void deleteQuestionWithDependencies(Question question) {
        // Delete all responses related to this question
        List<Response> responses = responseRepository.findByQuestionId(question.getId());
        for (Response response : responses) {
            responseRepository.delete(response);
        }

        // Delete all options for this question
        if (question.getOptions() != null) {
            for (Option option : question.getOptions()) {
                optionRepository.delete(option);
            }
        }

        // Delete the question itself
        questionRepository.delete(question);
    }

    /**
     * Crée un nouveau quiz avec le professeur spécifié comme propriétaire
     */
    @Transactional
    public Quiz createQuizForProfessor(Quiz quiz, Long professorId) {
        User professeur = userRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'id: " + professorId));

        // Vérifier que l'utilisateur est un professeur
        if (!"professeur".equalsIgnoreCase(professeur.getRole())) {
            throw new RuntimeException("L'utilisateur avec l'id " + professorId + " n'est pas un professeur");
        }

        quiz.setProfessor(professeur);
        return createCompleteQuiz(quiz);
    }

    /**
     * Met à jour un quiz si le professeur en est le propriétaire
     */
    @Transactional
    public Quiz updateProfessorQuiz(Long quizId, Quiz quizDetails, Long professorId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé avec l'id: " + quizId));

        // Vérifier que le professeur est le propriétaire du quiz
        if (quiz.getProfessor() == null || !quiz.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Le professeur avec l'id " + professorId + " n'est pas le propriétaire de ce quiz");
        }

        return updateQuiz(quizId, quizDetails);
    }

    /**
     * Supprime un quiz si le professeur en est le propriétaire
     */
    @Transactional
    public void deleteProfessorQuiz(Long quizId, Long professorId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé avec l'id: " + quizId));

        // Vérifier que le professeur est le propriétaire du quiz
        if (quiz.getProfessor() == null || !quiz.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Le professeur avec l'id " + professorId + " n'est pas le propriétaire de ce quiz");
        }

        // Use the improved delete method that handles dependencies
        deleteQuiz(quizId);
    }

    /**
     * Ajoute une question à un quiz existant
     */
    @Transactional
    public Question addQuestionToQuiz(Long quizId, Question question, Long professorId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé avec l'id: " + quizId));

        // Vérifier que le professeur est le propriétaire du quiz
        if (quiz.getProfessor() == null || !quiz.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Le professeur avec l'id " + professorId + " n'est pas le propriétaire de ce quiz");
        }

        question.setQuiz(quiz);
        Question savedQuestion = questionRepository.save(question);

        // Sauvegarder les options si fournies
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            for (Option option : question.getOptions()) {
                option.setQuestion(savedQuestion);
                optionRepository.save(option);
            }
        }

        return savedQuestion;
    }

    /**
     * Met à jour une question existante
     */
    @Transactional
    public Question updateQuestion(Long questionId, Question questionDetails, Long professorId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question non trouvée avec l'id: " + questionId));

        Quiz quiz = question.getQuiz();

        // Vérifier que le professeur est le propriétaire du quiz
        if (quiz.getProfessor() == null || !quiz.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Le professeur avec l'id " + professorId + " n'est pas le propriétaire de ce quiz");
        }

        question.setText(questionDetails.getText());

        // Mettre à jour les options si fournies
        if (questionDetails.getOptions() != null && !questionDetails.getOptions().isEmpty()) {
            // Delete existing responses for the options before deleting options
            for (Option option : question.getOptions()) {
                List<Response> responses = responseRepository.findAll().stream()
                        .filter(r -> r.getSelectedOption() != null && r.getSelectedOption().getId().equals(option.getId()))
                        .toList();

                for (Response response : responses) {
                    responseRepository.delete(response);
                }
            }

            // Now safely delete the options
            for (Option option : question.getOptions()) {
                optionRepository.delete(option);
            }

            // Ajouter les nouvelles options
            for (Option option : questionDetails.getOptions()) {
                option.setQuestion(question);
                optionRepository.save(option);
            }
            question.setOptions(questionDetails.getOptions());
        }

        return questionRepository.save(question);
    }

    /**
     * Supprime une question d'un quiz
     */
    @Transactional
    public void deleteQuestion(Long questionId, Long professorId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question non trouvée avec l'id: " + questionId));

        Quiz quiz = question.getQuiz();

        // Vérifier que le professeur est le propriétaire du quiz
        if (quiz.getProfessor() == null || !quiz.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Le professeur avec l'id " + professorId + " n'est pas le propriétaire de ce quiz");
        }

        deleteQuestionWithDependencies(question);
    }
}