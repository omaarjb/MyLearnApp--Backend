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
}
