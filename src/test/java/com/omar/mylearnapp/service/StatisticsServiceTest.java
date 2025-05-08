package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.Question;
import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.model.QuizAttempt;
import com.omar.mylearnapp.repository.QuestionRepository;
import com.omar.mylearnapp.repository.QuizAttemptRepository;
import com.omar.mylearnapp.repository.QuizRepository;
import com.omar.mylearnapp.repository.ResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ResponseRepository responseRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private Quiz testQuiz;
    private Question testQuestion;
    private QuizAttempt testAttempt;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setText("What is 2+2?");
    }

    @Test
    void getQuizStatistics_ShouldReturnCompleteStats_WhenQuizExists() {
        // Arrange
        Long quizId = 1L;

        // Mock repository responses
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.countAttemptsByQuizId(quizId)).thenReturn(10L);
        when(quizAttemptRepository.getAverageScoreByQuizId(quizId)).thenReturn(75.5);
        when(quizAttemptRepository.getAverageTimeByQuizId(quizId)).thenReturn(120.0);

        // Mock most missed questions response
        List<Map<String, Object>> missedQuestions = new ArrayList<>();
        Map<String, Object> missedQuestion = new HashMap<>();
        missedQuestion.put("questionId", 1L);
        missedQuestion.put("missedCount", 5L);
        missedQuestions.add(missedQuestion);

        when(responseRepository.findMostMissedQuestionsByQuizId(quizId)).thenReturn(missedQuestions);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act
        Map<String, Object> result = statisticsService.getQuizStatistics(quizId);

        // Assert
        assertNotNull(result);
        assertEquals(quizId, result.get("quizId"));
        assertEquals("Test Quiz", result.get("quizTitle"));
        assertEquals(10L, result.get("totalAttempts"));
        assertEquals(75.5, result.get("averageScore"));
        assertEquals(120.0, result.get("averageTimeSeconds"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> mostMissed = (List<Map<String, Object>>) result.get("mostMissedQuestions");
        assertNotNull(mostMissed);
        assertEquals(1, mostMissed.size());
        assertEquals("What is 2+2?", mostMissed.get(0).get("questionText"));
    }

    @Test
    void getQuizStatistics_ShouldThrowException_WhenQuizNotFound() {
        // Arrange
        Long quizId = 99L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            statisticsService.getQuizStatistics(quizId);
        });

        assertEquals("Quiz not found with id: 99", exception.getMessage());
    }

    @Test
    void getQuestionStatistics_ShouldReturnCompleteStats_WhenQuestionExists() {
        // Arrange
        Long questionId = 1L;

        // Mock repository responses
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(testQuestion));
        when(responseRepository.countTotalResponsesByQuestionId(questionId)).thenReturn(100L);
        when(responseRepository.countCorrectResponsesByQuestionId(questionId)).thenReturn(75L);

        // Mock option distribution response
        List<Map<String, Object>> optionCounts = new ArrayList<>();
        Map<String, Object> option1 = new HashMap<>();
        option1.put("optionText", "4");
        option1.put("responseCount", 75L);
        optionCounts.add(option1);

        when(responseRepository.countResponsesByOptionForQuestion(questionId)).thenReturn(optionCounts);

        // Act
        Map<String, Object> result = statisticsService.getQuestionStatistics(questionId);

        // Assert
        assertNotNull(result);
        assertEquals(questionId, result.get("questionId"));
        assertEquals("What is 2+2?", result.get("questionText"));
        assertEquals(100L, result.get("totalResponses"));
        assertEquals(75L, result.get("correctResponses"));
        assertEquals(75.0, result.get("correctPercentage"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> distribution = (List<Map<String, Object>>) result.get("optionDistribution");
        assertNotNull(distribution);
        assertEquals(1, distribution.size());
        assertEquals("4", distribution.get(0).get("optionText"));
    }

    @Test
    void getQuestionStatistics_ShouldHandleZeroResponses() {
        // Arrange
        Long questionId = 1L;

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(testQuestion));
        when(responseRepository.countTotalResponsesByQuestionId(questionId)).thenReturn(0L);
        when(responseRepository.countCorrectResponsesByQuestionId(questionId)).thenReturn(0L);
        when(responseRepository.countResponsesByOptionForQuestion(questionId)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = statisticsService.getQuestionStatistics(questionId);

        // Assert
        assertEquals(0.0, result.get("correctPercentage"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> distribution = (List<Map<String, Object>>) result.get("optionDistribution");
        assertTrue(distribution.isEmpty());
    }

    @Test
    void getSystemStatistics_ShouldReturnCompleteStats() {
        // Arrange
        when(quizRepository.count()).thenReturn(50L);
        when(quizAttemptRepository.count()).thenReturn(1000L);

        // Correct mock setup - properly closed parentheses
        when(quizAttemptRepository.findByStartTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Arrays.asList(testAttempt, testAttempt, testAttempt));

        // Act
        Map<String, Object> result = statisticsService.getSystemStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(50L, result.get("totalQuizzes"));
        assertEquals(1000L, result.get("totalAttempts"));
        assertEquals(3L, result.get("recentAttempts"));
    }


    @Test
    void getSystemStatistics_ShouldHandleEmptyData() {
        // Arrange
        when(quizRepository.count()).thenReturn(0L);
        when(quizAttemptRepository.count()).thenReturn(0L);

        // Use any() matchers for time parameters
        when(quizAttemptRepository.findByStartTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = statisticsService.getSystemStatistics();

        // Assert
        assertEquals(0L, result.get("totalQuizzes"));
        assertEquals(0L, result.get("totalAttempts"));
        assertEquals(0L, result.get("recentAttempts"));  // Note the L suffix
    }
}