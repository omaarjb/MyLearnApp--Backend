package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.Question;
import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.repository.QuestionRepository;
import com.omar.mylearnapp.repository.QuizAttemptRepository;
import com.omar.mylearnapp.repository.QuizRepository;
import com.omar.mylearnapp.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseRepository responseRepository;

    /**
     * Get statistics for a specific quiz
     */
    public Map<String, Object> getQuizStatistics(Long quizId) {
        Map<String, Object> statistics = new HashMap<>();

        // Basic quiz info
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
        statistics.put("quizId", quiz.getId());
        statistics.put("quizTitle", quiz.getTitle());

        // Attempt statistics
        long totalAttempts = quizAttemptRepository.countAttemptsByQuizId(quizId);
        Double averageScore = quizAttemptRepository.getAverageScoreByQuizId(quizId);
        Double averageTime = quizAttemptRepository.getAverageTimeByQuizId(quizId);

        statistics.put("totalAttempts", totalAttempts);
        statistics.put("averageScore", averageScore != null ? averageScore : 0);
        statistics.put("averageTimeSeconds", averageTime != null ? averageTime : 0);

        // Most missed questions
        List<Map<String, Object>> mostMissedQuestions = responseRepository.findMostMissedQuestionsByQuizId(quizId);

        // Enhance with question text
        List<Map<String, Object>> enhancedMissedQuestions = mostMissedQuestions.stream()
                .map(item -> {
                    Long questionId = ((Number) item.get("questionId")).longValue();
                    Question question = questionRepository.findById(questionId).orElse(null);

                    Map<String, Object> enhanced = new HashMap<>(item);
                    if (question != null) {
                        enhanced.put("questionText", question.getText());
                    }

                    return enhanced;
                })
                .collect(Collectors.toList());

        statistics.put("mostMissedQuestions", enhancedMissedQuestions);

        return statistics;
    }

    /**
     * Get statistics for a specific question
     */
    public Map<String, Object> getQuestionStatistics(Long questionId) {
        Map<String, Object> statistics = new HashMap<>();

        // Basic question info
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
        statistics.put("questionId", question.getId());
        statistics.put("questionText", question.getText());

        // Response statistics
        long totalResponses = responseRepository.countTotalResponsesByQuestionId(questionId);
        long correctResponses = responseRepository.countCorrectResponsesByQuestionId(questionId);

        statistics.put("totalResponses", totalResponses);
        statistics.put("correctResponses", correctResponses);
        statistics.put("correctPercentage", totalResponses > 0 ? (double) correctResponses / totalResponses * 100 : 0);

        // Option distribution
        List<Map<String, Object>> optionCounts = responseRepository.countResponsesByOptionForQuestion(questionId);
        statistics.put("optionDistribution", optionCounts);

        return statistics;
    }

    /**
     * Get overall system statistics
     */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Total counts
        long totalQuizzes = quizRepository.count();
        long totalAttempts = quizAttemptRepository.count();

        statistics.put("totalQuizzes", totalQuizzes);
        statistics.put("totalAttempts", totalAttempts);

        // Recent activity (last 7 days)
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        long recentAttempts = quizAttemptRepository.findByStartTimeBetween(oneWeekAgo, LocalDateTime.now()).size();

        statistics.put("recentAttempts", recentAttempts);

        return statistics;
    }
}