package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.*;
import com.omar.mylearnapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ResponseRepository responseRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuizAttemptService quizAttemptService;

    private User testUser;
    private Quiz testQuiz;
    private QuizAttempt testAttempt;
    private Question testQuestion;
    private Option testOption;
    private Response testResponse;

    @BeforeEach
    void setUp() {
        System.out.println("\n--- Setting up test environment ---");

        testUser = new User();
        testUser.setId(1L);
        testUser.setClerkId("user_123");
        testUser.setRole("student");
        System.out.println("Test user created with clerkId: " + testUser.getClerkId());

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setTimeLimit(300); // 5 minutes
        System.out.println("Test quiz created with time limit: " + testQuiz.getTimeLimit() + " seconds");

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setText("Test Question");
        testQuestion.setQuiz(testQuiz);
        System.out.println("Test question created: " + testQuestion.getText());

        testOption = new Option();
        testOption.setId(1L);
        testOption.setText("Test Option");
        testOption.setCorrect(true);
        testOption.setQuestion(testQuestion);
        System.out.println("Test option created: " + testOption.getText() + " (Correct: " + testOption.isCorrect() + ")");

        testQuestion.setOptions(Collections.singletonList(testOption));
        testQuiz.setQuestions(Collections.singletonList(testQuestion));

        testAttempt = new QuizAttempt();
        testAttempt.setId(1L);
        testAttempt.setUser(testUser);
        testAttempt.setQuiz(testQuiz);
        testAttempt.setStartTime(LocalDateTime.now().minusMinutes(1));
        testAttempt.setTotalQuestions(1);
        System.out.println("Test quiz attempt created with ID: " + testAttempt.getId());

        testResponse = new Response();
        testResponse.setId(1L);
        testResponse.setQuizAttempt(testAttempt);
        testResponse.setQuestion(testQuestion);
        testResponse.setSelectedOption(testOption);
        testResponse.setCorrect(true);
        System.out.println("Test response created for question ID: " + testResponse.getQuestion().getId());

        System.out.println("Test setup completed successfully");
    }

    @Test
    void startQuizAttempt_ShouldCreateNewAttempt() {
        // Arrange
        System.out.println("\n--- TEST: startQuizAttempt_ShouldCreateNewAttempt ---");
        System.out.println("Setting up mocks for starting quiz attempt");
        when(userRepository.findByClerkId("user_123")).thenReturn(Optional.of(testUser));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(testAttempt);

        // Act
        System.out.println("Calling quizAttemptService.startQuizAttempt('user_123', 1L)");
        QuizAttempt attempt = quizAttemptService.startQuizAttempt("user_123", 1L);

        // Assert
        System.out.println("Verifying quiz attempt was created");
        assertNotNull(attempt);
        System.out.println("Attempt ID: " + attempt.getId());
        assertEquals(testUser, attempt.getUser());
        assertEquals(testQuiz, attempt.getQuiz());
        assertNotNull(attempt.getStartTime());
        assertEquals(1, attempt.getTotalQuestions());
        verify(userRepository, times(1)).findByClerkId("user_123");
        verify(quizRepository, times(1)).findById(1L);
        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
        System.out.println("startQuizAttempt test completed successfully");
    }

    @Test
    void startQuizAttempt_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        System.out.println("\n--- TEST: startQuizAttempt_ShouldThrowException_WhenUserNotFound ---");
        System.out.println("Setting up mock for non-existent user clerkId");
        when(userRepository.findByClerkId("unknown_user")).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling startQuizAttempt with unknown user");
        Exception exception = assertThrows(RuntimeException.class, () ->
                quizAttemptService.startQuizAttempt("unknown_user", 1L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(userRepository, times(1)).findByClerkId("unknown_user");
        System.out.println("startQuizAttempt exception test completed successfully");
    }

    @Test
    void startQuizAttempt_ShouldThrowException_WhenQuizNotFound() {
        // Arrange
        System.out.println("\n--- TEST: startQuizAttempt_ShouldThrowException_WhenQuizNotFound ---");
        System.out.println("Setting up mocks for non-existent quiz ID");
        when(userRepository.findByClerkId("user_123")).thenReturn(Optional.of(testUser));
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling startQuizAttempt with unknown quiz ID");
        Exception exception = assertThrows(RuntimeException.class, () ->
                quizAttemptService.startQuizAttempt("user_123", 99L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(quizRepository, times(1)).findById(99L);
        System.out.println("startQuizAttempt exception test completed successfully");
    }

    @Test
    void submitQuizAttempt_ShouldCalculateScore_WhenWithinTimeLimit() {
        // Arrange
        System.out.println("\n--- TEST: submitQuizAttempt_ShouldCalculateScore_WhenWithinTimeLimit ---");
        System.out.println("Setting up mocks for submitting quiz attempt");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(optionRepository.findById(1L)).thenReturn(Optional.of(testOption));
        when(responseRepository.save(any(Response.class))).thenReturn(testResponse);
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(testAttempt);

        Map<Long, Long> responses = new HashMap<>();
        responses.put(1L, 1L); // Correct answer

        // Act
        System.out.println("Calling quizAttemptService.submitQuizAttempt(1L, responses)");
        QuizAttempt submittedAttempt = quizAttemptService.submitQuizAttempt(1L, responses);

        // Assert
        System.out.println("Verifying quiz attempt was submitted");
        assertNotNull(submittedAttempt);
        System.out.println("Attempt score: " + submittedAttempt.getScore());
        assertEquals(1, submittedAttempt.getScore());
        assertNotNull(submittedAttempt.getEndTime());
        assertNotNull(submittedAttempt.getTimeTakenSeconds());
        verify(quizAttemptRepository, times(1)).findById(1L);
        verify(responseRepository, times(1)).save(any(Response.class));
        verify(quizAttemptRepository, times(1)).save(testAttempt);
        System.out.println("submitQuizAttempt test completed successfully");
    }

    @Test
    void submitQuizAttempt_ShouldNotCalculateScore_WhenTimeLimitExceeded() {
        // Arrange
        System.out.println("\n--- TEST: submitQuizAttempt_ShouldNotCalculateScore_WhenTimeLimitExceeded ---");
        System.out.println("Setting up attempt with exceeded time limit");
        testAttempt.setStartTime(LocalDateTime.now().minusMinutes(10)); // Exceeds 5 minute limit

        System.out.println("Setting up mocks for time-exceeded submission");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(testAttempt);

        Map<Long, Long> responses = new HashMap<>();
        responses.put(1L, 1L); // Correct answer (but shouldn't be counted)

        // Act
        System.out.println("Calling quizAttemptService.submitQuizAttempt(1L, responses)");
        QuizAttempt submittedAttempt = quizAttemptService.submitQuizAttempt(1L, responses);

        // Assert
        System.out.println("Verifying score is 0 due to time limit exceeded");
        assertEquals(0, submittedAttempt.getScore());
        assertNotNull(submittedAttempt.getEndTime());
        verify(quizAttemptRepository, times(1)).findById(1L);
        verify(quizAttemptRepository, times(1)).save(testAttempt);
        System.out.println("submitQuizAttempt time limit test completed successfully");
    }

    @Test
    void hasTimeLimitExceeded_ShouldReturnTrue_WhenTimeLimitExceeded() {
        // Arrange
        System.out.println("\n--- TEST: hasTimeLimitExceeded_ShouldReturnTrue_WhenTimeLimitExceeded ---");
        System.out.println("Setting up attempt with exceeded time limit");
        testAttempt.setStartTime(LocalDateTime.now().minusMinutes(10)); // Exceeds 5 minute limit

        System.out.println("Setting up mock for time limit check");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));

        // Act
        System.out.println("Calling quizAttemptService.hasTimeLimitExceeded(1L)");
        boolean result = quizAttemptService.hasTimeLimitExceeded(1L);

        // Assert
        System.out.println("Verifying time limit was exceeded");
        assertTrue(result);
        verify(quizAttemptRepository, times(1)).findById(1L);
        System.out.println("hasTimeLimitExceeded test completed successfully");
    }

    @Test
    void hasTimeLimitExceeded_ShouldReturnFalse_WhenWithinTimeLimit() {
        // Arrange
        System.out.println("\n--- TEST: hasTimeLimitExceeded_ShouldReturnFalse_WhenWithinTimeLimit ---");
        System.out.println("Setting up attempt within time limit");
        testAttempt.setStartTime(LocalDateTime.now().minusMinutes(2)); // Within 5 minute limit

        System.out.println("Setting up mock for time limit check");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));

        // Act
        System.out.println("Calling quizAttemptService.hasTimeLimitExceeded(1L)");
        boolean result = quizAttemptService.hasTimeLimitExceeded(1L);

        // Assert
        System.out.println("Verifying time limit was not exceeded");
        assertFalse(result);
        verify(quizAttemptRepository, times(1)).findById(1L);
        System.out.println("hasTimeLimitExceeded test completed successfully");
    }

    @Test
    void autoSubmitExpiredAttempt_ShouldSubmitWithZeroScore() {
        // Arrange
        System.out.println("\n--- TEST: autoSubmitExpiredAttempt_ShouldSubmitWithZeroScore ---");
        System.out.println("Setting up mock for auto-submission");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(testAttempt);

        // Act
        System.out.println("Calling quizAttemptService.autoSubmitExpiredAttempt(1L)");
        QuizAttempt result = quizAttemptService.autoSubmitExpiredAttempt(1L);

        // Assert
        System.out.println("Verifying attempt was auto-submitted");
        assertNotNull(result.getEndTime());
        assertEquals(0, result.getScore());
        assertNotNull(result.getTimeTakenSeconds());
        verify(quizAttemptRepository, times(1)).findById(1L);
        verify(quizAttemptRepository, times(1)).save(testAttempt);
        System.out.println("autoSubmitExpiredAttempt test completed successfully");
    }

    @Test
    void getUserAttempts_ShouldReturnUserAttempts() {
        // Arrange
        System.out.println("\n--- TEST: getUserAttempts_ShouldReturnUserAttempts ---");
        System.out.println("Setting up mock for user attempts");
        when(userRepository.findByClerkId("user_123")).thenReturn(Optional.of(testUser));
        when(quizAttemptRepository.findByUserId(1L)).thenReturn(Collections.singletonList(testAttempt));

        // Act
        System.out.println("Calling quizAttemptService.getUserAttempts('user_123')");
        List<QuizAttempt> attempts = quizAttemptService.getUserAttempts("user_123");

        // Assert
        System.out.println("Verifying user attempts returned");
        assertEquals(1, attempts.size());
        assertEquals(testAttempt, attempts.get(0));
        verify(userRepository, times(1)).findByClerkId("user_123");
        verify(quizAttemptRepository, times(1)).findByUserId(1L);
        System.out.println("getUserAttempts test completed successfully");
    }

    @Test
    void getRecentUserAttempts_ShouldReturnRecentAttempts() {
        // Arrange
        System.out.println("\n--- TEST: getRecentUserAttempts_ShouldReturnRecentAttempts ---");
        System.out.println("Setting up mock for recent user attempts");
        when(userRepository.findByClerkId("user_123")).thenReturn(Optional.of(testUser));
        when(quizAttemptRepository.findRecentAttemptsByUserId(1L)).thenReturn(Collections.singletonList(testAttempt));

        // Act
        System.out.println("Calling quizAttemptService.getRecentUserAttempts('user_123')");
        List<QuizAttempt> attempts = quizAttemptService.getRecentUserAttempts("user_123");

        // Assert
        System.out.println("Verifying recent attempts returned");
        assertEquals(1, attempts.size());
        assertEquals(testAttempt, attempts.get(0));
        verify(userRepository, times(1)).findByClerkId("user_123");
        verify(quizAttemptRepository, times(1)).findRecentAttemptsByUserId(1L);
        System.out.println("getRecentUserAttempts test completed successfully");
    }

    @Test
    void getQuizAttempts_ShouldReturnQuizAttempts() {
        // Arrange
        System.out.println("\n--- TEST: getQuizAttempts_ShouldReturnQuizAttempts ---");
        System.out.println("Setting up mock for quiz attempts");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.findByQuizId(1L)).thenReturn(Collections.singletonList(testAttempt));

        // Act
        System.out.println("Calling quizAttemptService.getQuizAttempts(1L)");
        List<QuizAttempt> attempts = quizAttemptService.getQuizAttempts(1L);

        // Assert
        System.out.println("Verifying quiz attempts returned");
        assertEquals(1, attempts.size());
        assertEquals(testAttempt, attempts.get(0));
        verify(quizRepository, times(1)).findById(1L);
        verify(quizAttemptRepository, times(1)).findByQuizId(1L);
        System.out.println("getQuizAttempts test completed successfully");
    }

    @Test
    void getAttemptById_ShouldReturnAttempt_WhenExists() {
        // Arrange
        System.out.println("\n--- TEST: getAttemptById_ShouldReturnAttempt_WhenExists ---");
        System.out.println("Setting up mock for attempt ID: 1");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));

        // Act
        System.out.println("Calling quizAttemptService.getAttemptById(1L)");
        Optional<QuizAttempt> foundAttempt = quizAttemptService.getAttemptById(1L);

        // Assert
        System.out.println("Verifying attempt was found");
        assertTrue(foundAttempt.isPresent());
        assertEquals(testAttempt, foundAttempt.get());
        verify(quizAttemptRepository, times(1)).findById(1L);
        System.out.println("getAttemptById test completed successfully");
    }

    @Test
    void getAttemptById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        System.out.println("\n--- TEST: getAttemptById_ShouldReturnEmpty_WhenNotExists ---");
        System.out.println("Setting up mock for non-existent attempt ID: 99");
        when(quizAttemptRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        System.out.println("Calling quizAttemptService.getAttemptById(99L)");
        Optional<QuizAttempt> foundAttempt = quizAttemptService.getAttemptById(99L);

        // Assert
        System.out.println("Verifying attempt was not found");
        assertFalse(foundAttempt.isPresent());
        verify(quizAttemptRepository, times(1)).findById(99L);
        System.out.println("getAttemptById empty test completed successfully");
    }

    @Test
    void getUserAttemptsForQuiz_ShouldReturnAttempts() {
        // Arrange
        System.out.println("\n--- TEST: getUserAttemptsForQuiz_ShouldReturnAttempts ---");
        System.out.println("Setting up mocks for user attempts for quiz");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.findByUserIdAndQuizId(1L, 1L)).thenReturn(Collections.singletonList(testAttempt));

        // Act
        System.out.println("Calling quizAttemptService.getUserAttemptsForQuiz(1L, 1L)");
        List<QuizAttempt> attempts = quizAttemptService.getUserAttemptsForQuiz(1L, 1L);

        // Assert
        System.out.println("Verifying user attempts for quiz returned");
        assertEquals(1, attempts.size());
        assertEquals(testAttempt, attempts.get(0));
        verify(userRepository, times(1)).findById(1L);
        verify(quizRepository, times(1)).findById(1L);
        verify(quizAttemptRepository, times(1)).findByUserIdAndQuizId(1L, 1L);
        System.out.println("getUserAttemptsForQuiz test completed successfully");
    }

    @Test
    void getResponsesForAttempt_ShouldReturnResponses() {
        // Arrange
        System.out.println("\n--- TEST: getResponsesForAttempt_ShouldReturnResponses ---");
        System.out.println("Setting up mock for responses");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));
        when(responseRepository.findByQuizAttemptId(1L)).thenReturn(Collections.singletonList(testResponse));

        // Act
        System.out.println("Calling quizAttemptService.getResponsesForAttempt(1L)");
        List<Response> responses = quizAttemptService.getResponsesForAttempt(1L);

        // Assert
        System.out.println("Verifying responses returned");
        assertEquals(1, responses.size());
        assertEquals(testResponse, responses.get(0));
        verify(quizAttemptRepository, times(1)).findById(1L);
        verify(responseRepository, times(1)).findByQuizAttemptId(1L);
        System.out.println("getResponsesForAttempt test completed successfully");
    }

    @Test
    void getResponseForQuestion_ShouldReturnResponse() {
        // Arrange
        System.out.println("\n--- TEST: getResponseForQuestion_ShouldReturnResponse ---");
        System.out.println("Setting up mock for question response");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(responseRepository.findByAttemptIdAndQuestionId(1L, 1L)).thenReturn(testResponse);

        // Act
        System.out.println("Calling quizAttemptService.getResponseForQuestion(1L, 1L)");
        Response response = quizAttemptService.getResponseForQuestion(1L, 1L);

        // Assert
        System.out.println("Verifying response returned");
        assertNotNull(response);
        assertEquals(testResponse, response);
        verify(quizAttemptRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).findById(1L);
        verify(responseRepository, times(1)).findByAttemptIdAndQuestionId(1L, 1L);
        System.out.println("getResponseForQuestion test completed successfully");
    }

    @Test
    void deleteQuizAttempt_ShouldDeleteAttemptAndResponses() {
        // Arrange
        System.out.println("\n--- TEST: deleteQuizAttempt_ShouldDeleteAttemptAndResponses ---");
        System.out.println("Setting up mocks for attempt deletion");
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(testAttempt));
        when(responseRepository.findByQuizAttemptId(1L)).thenReturn(Collections.singletonList(testResponse));

        // Act
        System.out.println("Calling quizAttemptService.deleteQuizAttempt(1L)");
        quizAttemptService.deleteQuizAttempt(1L);

        // Assert
        System.out.println("Verifying attempt and responses were deleted");
        verify(responseRepository, times(1)).deleteAll(Collections.singletonList(testResponse));
        verify(quizAttemptRepository, times(1)).deleteById(1L);
        System.out.println("deleteQuizAttempt test completed successfully");
    }
}