package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.Option;
import com.omar.mylearnapp.model.Question;
import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.repository.OptionRepository;
import com.omar.mylearnapp.repository.QuestionRepository;
import com.omar.mylearnapp.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuestionService questionService;

    private Question testQuestion;
    private Quiz testQuiz;
    private Option testOption1;
    private Option testOption2;
    private List<Question> questionList;
    private Long testQuizId;
    private Long testQuestionId;

    @BeforeEach
    void setUp() {
        System.out.println("\n--- Setting up test environment ---");

        // Create test quiz
        testQuizId = 1L;
        testQuiz = new Quiz();
        testQuiz.setId(testQuizId);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Quiz for testing");

        // Create test question
        testQuestionId = 1L;
        testQuestion = new Question();
        testQuestion.setId(testQuestionId);
        testQuestion.setText("What is JUnit?");
        testQuestion.setQuiz(testQuiz);

        // Create test options
        testOption1 = new Option();
        testOption1.setId(1L);
        testOption1.setText("A testing framework");
        testOption1.setCorrect(true);
        testOption1.setQuestion(testQuestion);

        testOption2 = new Option();
        testOption2.setId(2L);
        testOption2.setText("A database library");
        testOption2.setCorrect(false);
        testOption2.setQuestion(testQuestion);

        // Add options to question
        List<Option> options = new ArrayList<>();
        options.add(testOption1);
        options.add(testOption2);
        testQuestion.setOptions(options);

        // Create question list for getAllQuestions and getQuestionsByQuizId tests
        questionList = new ArrayList<>();
        questionList.add(testQuestion);

        System.out.println("Test quiz created with ID: " + testQuiz.getId() +
                ", Title: " + testQuiz.getTitle());
        System.out.println("Test question created with ID: " + testQuestion.getId() +
                ", Content: " + testQuestion.getText());
        System.out.println("Test options created: " + options.size() + " options");
        System.out.println("Test setup completed successfully");
    }

    @Test
    void getAllQuestions_ShouldReturnAllQuestions() {
        // Arrange
        System.out.println("\n--- TEST: getAllQuestions_ShouldReturnAllQuestions ---");
        System.out.println("Setting up mock for questionRepository.findAll()");
        when(questionRepository.findAll()).thenReturn(questionList);

        // Act
        System.out.println("Calling questionService.getAllQuestions()");
        List<Question> result = questionService.getAllQuestions();

        // Assert
        System.out.println("Verifying results");
        assertEquals(1, result.size());
        assertEquals(testQuestion, result.get(0));

        System.out.println("Verifying questionRepository.findAll() was called");
        verify(questionRepository, times(1)).findAll();

        System.out.println("getAllQuestions test completed successfully");
    }

    @Test
    void getQuestionsByQuizId_ShouldReturnQuizQuestions() {
        // Arrange
        System.out.println("\n--- TEST: getQuestionsByQuizId_ShouldReturnQuizQuestions ---");
        System.out.println("Setting up mock for questionRepository.findByQuizId(" + testQuizId + ")");
        when(questionRepository.findByQuizId(testQuizId)).thenReturn(questionList);

        // Act
        System.out.println("Calling questionService.getQuestionsByQuizId(" + testQuizId + ")");
        List<Question> result = questionService.getQuestionsByQuizId(testQuizId);

        // Assert
        System.out.println("Verifying results");
        assertEquals(1, result.size());
        assertEquals(testQuestion, result.get(0));

        System.out.println("Verifying questionRepository.findByQuizId() was called");
        verify(questionRepository, times(1)).findByQuizId(testQuizId);

        System.out.println("getQuestionsByQuizId test completed successfully");
    }

    @Test
    void getQuestionById_ShouldReturnQuestion_WhenQuestionExists() {
        // Arrange
        System.out.println("\n--- TEST: getQuestionById_ShouldReturnQuestion_WhenQuestionExists ---");
        System.out.println("Setting up mock for questionRepository.findById(" + testQuestionId + ")");
        when(questionRepository.findById(testQuestionId)).thenReturn(Optional.of(testQuestion));

        // Act
        System.out.println("Calling questionService.getQuestionById(" + testQuestionId + ")");
        Question result = questionService.getQuestionById(testQuestionId);

        // Assert
        System.out.println("Verifying result");
        assertEquals(testQuestion, result);

        System.out.println("Verifying questionRepository.findById() was called");
        verify(questionRepository, times(1)).findById(testQuestionId);

        System.out.println("getQuestionById test completed successfully");
    }

    @Test
    void getQuestionById_ShouldThrowException_WhenQuestionDoesNotExist() {
        // Arrange
        System.out.println("\n--- TEST: getQuestionById_ShouldThrowException_WhenQuestionDoesNotExist ---");
        Long nonExistentId = 99L;
        System.out.println("Setting up mock for questionRepository.findById(" + nonExistentId + ") to return empty");
        when(questionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Verifying that exception is thrown when calling getQuestionById with non-existent ID");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            questionService.getQuestionById(nonExistentId);
        });

        System.out.println("Verifying exception message");
        assertEquals("Question not found with id: " + nonExistentId, exception.getMessage());

        System.out.println("Verifying questionRepository.findById() was called");
        verify(questionRepository, times(1)).findById(nonExistentId);

        System.out.println("getQuestionById exception test completed successfully");
    }

    @Test
    void createCompleteQuestion_ShouldCreateQuestionWithOptions_WhenQuizExists() {
        // Arrange
        System.out.println("\n--- TEST: createCompleteQuestion_ShouldCreateQuestionWithOptions_WhenQuizExists ---");
        System.out.println("Setting up mock for quizRepository.findById(" + testQuizId + ")");
        when(quizRepository.findById(testQuizId)).thenReturn(Optional.of(testQuiz));

        System.out.println("Setting up mock for questionRepository.save()");
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        System.out.println("Setting up mock for optionRepository.save()");
        when(optionRepository.save(any(Option.class))).thenReturn(testOption1).thenReturn(testOption2);

        // Act
        System.out.println("Calling questionService.createCompleteQuestion() with test data");
        Question result = questionService.createCompleteQuestion(testQuestion);

        // Assert
        System.out.println("Verifying result");
        assertEquals(testQuestion, result);

        System.out.println("Verifying quizRepository.findById() was called");
        verify(quizRepository, times(1)).findById(testQuizId);

        System.out.println("Verifying questionRepository.save() was called");
        verify(questionRepository, times(1)).save(testQuestion);

        System.out.println("Verifying optionRepository.save() was called for each option");
        verify(optionRepository, times(2)).save(any(Option.class));

        System.out.println("createCompleteQuestion test completed successfully");
    }

    @Test
    void createCompleteQuestion_ShouldThrowException_WhenQuizDoesNotExist() {
        // Arrange
        System.out.println("\n--- TEST: createCompleteQuestion_ShouldThrowException_WhenQuizDoesNotExist ---");
        Long nonExistentQuizId = 99L;
        testQuiz.setId(nonExistentQuizId);
        testQuestion.setQuiz(testQuiz);

        System.out.println("Setting up mock for quizRepository.findById(" + nonExistentQuizId + ") to return empty");
        when(quizRepository.findById(nonExistentQuizId)).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Verifying that exception is thrown when calling createCompleteQuestion with non-existent quiz ID");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            questionService.createCompleteQuestion(testQuestion);
        });

        System.out.println("Verifying exception message");
        assertEquals("Quiz not found with id: " + nonExistentQuizId, exception.getMessage());

        System.out.println("Verifying quizRepository.findById() was called");
        verify(quizRepository, times(1)).findById(nonExistentQuizId);

        System.out.println("Verifying questionRepository.save() was NOT called");
        verify(questionRepository, never()).save(any(Question.class));

        System.out.println("Verifying optionRepository.save() was NOT called");
        verify(optionRepository, never()).save(any(Option.class));

        System.out.println("createCompleteQuestion exception test completed successfully");
    }

    @Test
    void createCompleteQuestion_ShouldCreateQuestionWithoutOptions_WhenNoOptionsProvided() {
        // Arrange
        System.out.println("\n--- TEST: createCompleteQuestion_ShouldCreateQuestionWithoutOptions_WhenNoOptionsProvided ---");
        Question questionWithoutOptions = new Question();
        questionWithoutOptions.setId(2L);
        questionWithoutOptions.setText("What is Mockito?");
        questionWithoutOptions.setQuiz(testQuiz);
        questionWithoutOptions.setOptions(new ArrayList<>());

        System.out.println("Setting up mock for quizRepository.findById(" + testQuizId + ")");
        when(quizRepository.findById(testQuizId)).thenReturn(Optional.of(testQuiz));

        System.out.println("Setting up mock for questionRepository.save()");
        when(questionRepository.save(any(Question.class))).thenReturn(questionWithoutOptions);

        // Act
        System.out.println("Calling questionService.createCompleteQuestion() with test data without options");
        Question result = questionService.createCompleteQuestion(questionWithoutOptions);

        // Assert
        System.out.println("Verifying result");
        assertEquals(questionWithoutOptions, result);

        System.out.println("Verifying quizRepository.findById() was called");
        verify(quizRepository, times(1)).findById(testQuizId);

        System.out.println("Verifying questionRepository.save() was called");
        verify(questionRepository, times(1)).save(questionWithoutOptions);

        System.out.println("Verifying optionRepository.save() was NOT called");
        verify(optionRepository, never()).save(any(Option.class));

        System.out.println("createCompleteQuestion without options test completed successfully");
    }

    @Test
    void deleteQuestion_ShouldDeleteQuestion() {
        // Arrange
        System.out.println("\n--- TEST: deleteQuestion_ShouldDeleteQuestion ---");
        System.out.println("Setting up test for deletion of question with ID: " + testQuestionId);
        doNothing().when(questionRepository).deleteById(testQuestionId);

        // Act
        System.out.println("Calling questionService.deleteQuestion(" + testQuestionId + ")");
        questionService.deleteQuestion(testQuestionId);

        // Assert
        System.out.println("Verifying questionRepository.deleteById() was called");
        verify(questionRepository, times(1)).deleteById(testQuestionId);

        System.out.println("deleteQuestion test completed successfully");
    }

    @Test
    void createCompleteQuestion_ShouldCreateQuestionWithoutQuiz_WhenQuizIsNull() {
        // Arrange
        System.out.println("\n--- TEST: createCompleteQuestion_ShouldCreateQuestionWithoutQuiz_WhenQuizIsNull ---");
        Question questionWithoutQuiz = new Question();
        questionWithoutQuiz.setId(3L);
        questionWithoutQuiz.setText("Standalone question");
        questionWithoutQuiz.setQuiz(null);

        System.out.println("Setting up mock for questionRepository.save()");
        when(questionRepository.save(any(Question.class))).thenReturn(questionWithoutQuiz);

        // Act
        System.out.println("Calling questionService.createCompleteQuestion() with test data without quiz");
        Question result = questionService.createCompleteQuestion(questionWithoutQuiz);

        // Assert
        System.out.println("Verifying result");
        assertEquals(questionWithoutQuiz, result);

        System.out.println("Verifying quizRepository.findById() was NOT called");
        verify(quizRepository, never()).findById(any());

        System.out.println("Verifying questionRepository.save() was called");
        verify(questionRepository, times(1)).save(questionWithoutQuiz);

        System.out.println("createCompleteQuestion without quiz test completed successfully");
    }
}