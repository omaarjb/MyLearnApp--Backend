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

    public List<Quiz> getQuizzesByDifficulty(String difficulty) {
        return quizRepository.findByDifficulty(difficulty);
    }

    public List<Quiz> getQuizzesByCategory(String category) {
        return quizRepository.findByCategory(category);
    }

    public List<Quiz> getQuizzesByProfessor(Long professorId) {
        return quizRepository.findByProfessorId(professorId);
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
    public List<Quiz> getQuizzesByTopic(String topicName) {
        return quizRepository.findByTopic_Name(topicName);
    }


    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        quizRepository.delete(quiz);
    }
}
