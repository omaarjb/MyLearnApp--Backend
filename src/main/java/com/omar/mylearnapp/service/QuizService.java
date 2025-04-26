package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.*;
import com.omar.mylearnapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private UserRepository  userRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Transactional
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz createCompleteQuiz(Quiz quiz) {
        // Validate topic
        if (quiz.getTopic() != null && quiz.getTopic().getId() != null) {
            Topic topic = topicRepository.findById(quiz.getTopic().getId())
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + quiz.getTopic().getId()));
            quiz.setTopic(topic);
        }

        // Save the quiz first
        Quiz savedQuiz = quizRepository.save(quiz);

        // Save questions and options
        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
            for (Question question : quiz.getQuestions()) {
                question.setQuiz(savedQuiz);
                Question savedQuestion = questionRepository.save(question);

                if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                    for (Option option : question.getOptions()) {
                        option.setQuestion(savedQuestion);
                        optionRepository.save(option);
                    }
                }
            }
        }

        // Refresh the quiz to get all the relationships
        return quizRepository.findById(savedQuiz.getId()).orElse(savedQuiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> getQuizzesByCategory(String category) {
        return quizRepository.findByCategory(category);
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
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

    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        quizRepository.delete(quiz);
    }

    public List<Quiz> getQuizzesByTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + topicId));
        return quizRepository.findByTopicId(topic.getId());
    }

    public List<Quiz> getQuizzesByDifficulty(String difficulty) {
        return quizRepository.findByDifficulty(difficulty);
    }

    public List<Quiz> getQuizzesByProfessor(Long professorId) {
        return quizRepository.findByProfessorId(professorId);
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

        quizRepository.delete(quiz);
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



        // Debug: Print incoming options
        System.out.println("Received question details: " + questionDetails);
        System.out.println("Received options count: " +
                (questionDetails.getOptions() != null ? questionDetails.getOptions().size() : 0));

        if (questionDetails.getOptions() != null) {
            for (int i = 0; i < questionDetails.getOptions().size(); i++) {
                Option option = questionDetails.getOptions().get(i);
                System.out.println("Option " + (i+1) + ": Text = " + option.getText() +
                        ", Correct = " + option.isCorrect());
            }
        }

        // Mettre à jour les options si fournies
        if (questionDetails.getOptions() != null && !questionDetails.getOptions().isEmpty()) {
            // Supprimer les options existantes
            for (Option option : question.getOptions()) {
                optionRepository.delete(option);
            }

            // Ajouter les nouvelles options
            for (Option option : questionDetails.getOptions()) {
                option.setQuestion(question);
                System.out.println("Saving option: " + option.getText() + ", correct = " + option.isCorrect());
                optionRepository.save(option);
            }
            question.setOptions(questionDetails.getOptions());
        }

        // Final state debug
        Question savedQuestion = questionRepository.save(question);
        System.out.println("Saved question options: ");
        if (savedQuestion.getOptions() != null) {
            for (Option option : savedQuestion.getOptions()) {
                System.out.println("Saved option: " + option.getText() + ", correct = " + option.isCorrect());
            }
        }

        return savedQuestion;
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

        // Supprimer d'abord toutes les options
        if (question.getOptions() != null) {
            for (Option option : question.getOptions()) {
                optionRepository.delete(option);
            }
        }

        // Supprimer toutes les réponses à cette question
        if (question.getResponses() != null) {
            for (Response response : question.getResponses()) {
                responseRepository.delete(response);
            }
        }

        questionRepository.delete(question);
    }
}
