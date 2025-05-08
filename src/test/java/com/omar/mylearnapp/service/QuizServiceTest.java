package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.*;
import com.omar.mylearnapp.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResponseRepository responseRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @InjectMocks
    private QuizService quizService;

    private Quiz testQuiz;
    private Question testQuestion;
    private Option testOption;
    private User testProfessor;
    private Topic testTopic;

    @BeforeEach
    void setUp() {
        System.out.println("\n--- Setting up test environment ---");

        testTopic = new Topic();
        testTopic.setId(1L);
        testTopic.setName("Test Topic");
        System.out.println("Test topic created: " + testTopic.getName());

        testProfessor = new User();
        testProfessor.setId(1L);
        testProfessor.setRole("professeur");
        System.out.println("Test professor created with role: " + testProfessor.getRole());

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setTopic(testTopic);
        testQuiz.setProfessor(testProfessor);
        System.out.println("Test quiz created: " + testQuiz.getTitle());

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
        System.out.println("Test setup completed successfully");
    }

    @Test
    void getAllQuizzes_ShouldReturnAllQuizzes() {
        // Arrange
        System.out.println("\n--- TEST: getAllQuizzes_ShouldReturnAllQuizzes ---");
        System.out.println("Setting up mock for getAllQuizzes test");
        when(quizRepository.findAll()).thenReturn(Collections.singletonList(testQuiz));

        // Act
        System.out.println("Calling quizService.getAllQuizzes()");
        List<Quiz> quizzes = quizService.getAllQuizzes();

        // Assert
        System.out.println("Verifying results - Expected 1 quiz");
        assertEquals(1, quizzes.size());
        System.out.println("Quiz title: " + quizzes.get(0).getTitle());
        assertEquals(testQuiz, quizzes.get(0));
        verify(quizRepository, times(1)).findAll();
        System.out.println("getAllQuizzes test completed successfully");
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenExists() {
        // Arrange
        System.out.println("\n--- TEST: getQuizById_ShouldReturnQuiz_WhenExists ---");
        System.out.println("Setting up mock for findById with ID: 1");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act
        System.out.println("Calling quizService.getQuizById(1L)");
        Optional<Quiz> foundQuiz = quizService.getQuizById(1L);

        // Assert
        System.out.println("Verifying quiz was found");
        assertTrue(foundQuiz.isPresent());
        System.out.println("Quiz found with title: " + foundQuiz.get().getTitle());
        assertEquals(testQuiz, foundQuiz.get());
        verify(quizRepository, times(1)).findById(1L);
        System.out.println("getQuizById test completed successfully");
    }

    @Test
    void getQuizById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        System.out.println("\n--- TEST: getQuizById_ShouldReturnEmpty_WhenNotExists ---");
        System.out.println("Setting up mock for findById with non-existent ID: 99");
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        System.out.println("Calling quizService.getQuizById(99L)");
        Optional<Quiz> foundQuiz = quizService.getQuizById(99L);

        // Assert
        System.out.println("Verifying quiz was not found");
        assertFalse(foundQuiz.isPresent());
        verify(quizRepository, times(1)).findById(99L);
        System.out.println("getQuizById_ShouldReturnEmpty test completed successfully");
    }

    @Test
    void getQuizzesByTopic_WithTopicId_ShouldReturnQuizzes() {
        // Arrange
        System.out.println("\n--- TEST: getQuizzesByTopic_WithTopicId_ShouldReturnQuizzes ---");
        System.out.println("Setting up mocks for topic ID: 1");
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(quizRepository.findByTopicId(1L)).thenReturn(Collections.singletonList(testQuiz));

        // Act
        System.out.println("Calling quizService.getQuizzesByTopic(1L)");
        List<Quiz> quizzes = quizService.getQuizzesByTopic(1L);

        // Assert
        System.out.println("Verifying results - Expected 1 quiz for topic: " + testTopic.getName());
        assertEquals(1, quizzes.size());
        System.out.println("Quiz found: " + quizzes.get(0).getTitle());
        assertEquals(testQuiz, quizzes.get(0));
        verify(topicRepository, times(1)).findById(1L);
        verify(quizRepository, times(1)).findByTopicId(1L);
        System.out.println("getQuizzesByTopic test completed successfully");
    }

    @Test
    void getQuizzesByTopic_WithTopicId_ShouldThrowException_WhenTopicNotFound() {
        // Arrange
        System.out.println("\n--- TEST: getQuizzesByTopic_WithTopicId_ShouldThrowException_WhenTopicNotFound ---");
        System.out.println("Setting up mock for non-existent topic ID: 99");
        when(topicRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling getQuizzesByTopic(99L)");
        Exception exception = assertThrows(RuntimeException.class, () -> quizService.getQuizzesByTopic(99L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(topicRepository, times(1)).findById(99L);
        System.out.println("getQuizzesByTopic exception test completed successfully");
    }

    @Test
    void getQuizzesByTopic_WithTopicName_ShouldReturnQuizzes() {
        // Arrange
        System.out.println("\n--- TEST: getQuizzesByTopic_WithTopicName_ShouldReturnQuizzes ---");
        System.out.println("Setting up mock for topic name: 'Test Topic'");
        when(quizRepository.findByTopic_Name("Test Topic")).thenReturn(Collections.singletonList(testQuiz));

        // Act
        System.out.println("Calling quizService.getQuizzesByTopic('Test Topic')");
        List<Quiz> quizzes = quizService.getQuizzesByTopic("Test Topic");

        // Assert
        System.out.println("Verifying results - Expected 1 quiz for topic name: 'Test Topic'");
        assertEquals(1, quizzes.size());
        System.out.println("Quiz found: " + quizzes.get(0).getTitle());
        assertEquals(testQuiz, quizzes.get(0));
        verify(quizRepository, times(1)).findByTopic_Name("Test Topic");
        System.out.println("getQuizzesByTopic by name test completed successfully");
    }

    @Test
    void getQuizzesByDifficulty_ShouldReturnQuizzes() {
        // Arrange
        System.out.println("\n--- TEST: getQuizzesByDifficulty_ShouldReturnQuizzes ---");
        System.out.println("Setting up mock for difficulty: 'Easy'");
        when(quizRepository.findByDifficulty("Easy")).thenReturn(Collections.singletonList(testQuiz));

        // Act
        System.out.println("Calling quizService.getQuizzesByDifficulty('Easy')");
        List<Quiz> quizzes = quizService.getQuizzesByDifficulty("Easy");

        // Assert
        System.out.println("Verifying results - Expected 1 quiz with difficulty 'Easy'");
        assertEquals(1, quizzes.size());
        System.out.println("Quiz found: " + quizzes.get(0).getTitle());
        assertEquals(testQuiz, quizzes.get(0));
        verify(quizRepository, times(1)).findByDifficulty("Easy");
        System.out.println("getQuizzesByDifficulty test completed successfully");
    }

    @Test
    void getQuizzesByCategory_ShouldReturnQuizzes() {
        // Arrange
        System.out.println("\n--- TEST: getQuizzesByCategory_ShouldReturnQuizzes ---");
        System.out.println("Setting up mock for category: 'Science'");
        when(quizRepository.findByCategory("Science")).thenReturn(Collections.singletonList(testQuiz));

        // Act
        System.out.println("Calling quizService.getQuizzesByCategory('Science')");
        List<Quiz> quizzes = quizService.getQuizzesByCategory("Science");

        // Assert
        System.out.println("Verifying results - Expected 1 quiz with category 'Science'");
        assertEquals(1, quizzes.size());
        System.out.println("Quiz found: " + quizzes.get(0).getTitle());
        assertEquals(testQuiz, quizzes.get(0));
        verify(quizRepository, times(1)).findByCategory("Science");
        System.out.println("getQuizzesByCategory test completed successfully");
    }

    @Test
    void getQuizzesByProfessor_ShouldReturnQuizzes() {
        // Arrange
        System.out.println("\n--- TEST: getQuizzesByProfessor_ShouldReturnQuizzes ---");
        System.out.println("Setting up mock for professor ID: 1");
        when(quizRepository.findByProfessorId(1L)).thenReturn(Collections.singletonList(testQuiz));

        // Act
        System.out.println("Calling quizService.getQuizzesByProfessor(1L)");
        List<Quiz> quizzes = quizService.getQuizzesByProfessor(1L);

        // Assert
        System.out.println("Verifying results - Expected 1 quiz by professor with ID: 1");
        assertEquals(1, quizzes.size());
        System.out.println("Quiz found: " + quizzes.get(0).getTitle());
        assertEquals(testQuiz, quizzes.get(0));
        verify(quizRepository, times(1)).findByProfessorId(1L);
        System.out.println("getQuizzesByProfessor test completed successfully");
    }

    @Test
    void createQuiz_ShouldSaveAndReturnQuiz() {
        // Arrange
        System.out.println("\n--- TEST: createQuiz_ShouldSaveAndReturnQuiz ---");
        System.out.println("Setting up mock for quiz save operation");
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        // Act
        System.out.println("Calling quizService.createQuiz()");
        Quiz createdQuiz = quizService.createQuiz(testQuiz);

        // Assert
        System.out.println("Verifying quiz was created");
        assertEquals(testQuiz, createdQuiz);
        System.out.println("Created quiz title: " + createdQuiz.getTitle());
        verify(quizRepository, times(1)).save(testQuiz);
        System.out.println("createQuiz test completed successfully");
    }

    @Test
    void createCompleteQuiz_ShouldSaveQuizWithQuestionsAndOptions() {
        // Arrange
        System.out.println("\n--- TEST: createCompleteQuiz_ShouldSaveQuizWithQuestionsAndOptions ---");
        System.out.println("Setting up mocks for complete quiz creation");
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);
        when(optionRepository.save(any(Option.class))).thenReturn(testOption);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act
        System.out.println("Calling quizService.createCompleteQuiz()");
        Quiz createdQuiz = quizService.createCompleteQuiz(testQuiz);

        // Assert
        System.out.println("Verifying complete quiz was created");
        assertNotNull(createdQuiz);
        System.out.println("Created quiz title: " + createdQuiz.getTitle());
        assertEquals(testQuiz, createdQuiz);
        System.out.println("Verifying repository calls");
        verify(quizRepository, times(1)).save(testQuiz);
        verify(questionRepository, atLeastOnce()).save(any(Question.class));
        verify(optionRepository, atLeastOnce()).save(any(Option.class));
        System.out.println("createCompleteQuiz test completed successfully");
    }

    @Test
    void updateQuiz_ShouldUpdateAndReturnQuiz() {
        // Arrange
        System.out.println("\n--- TEST: updateQuiz_ShouldUpdateAndReturnQuiz ---");
        System.out.println("Creating updated quiz object");
        Quiz updatedQuiz = new Quiz();
        updatedQuiz.setTitle("Updated Quiz");
        updatedQuiz.setDescription("Updated Description");
        updatedQuiz.setDifficulty("Hard");
        updatedQuiz.setCategory("Math");
        updatedQuiz.setTimeLimit(30);
        System.out.println("Updated quiz properties: Title=" + updatedQuiz.getTitle() +
                ", Difficulty=" + updatedQuiz.getDifficulty() +
                ", Category=" + updatedQuiz.getCategory());

        System.out.println("Setting up mocks for quiz update");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        when(quizRepository.save(any(Quiz.class))).thenReturn(updatedQuiz);

        // Act
        System.out.println("Calling quizService.updateQuiz(1L, updatedQuiz)");
        Quiz result = quizService.updateQuiz(1L, updatedQuiz);

        // Assert
        System.out.println("Verifying quiz was updated");
        assertEquals("Updated Quiz", result.getTitle());
        System.out.println("Updated quiz title: " + result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        System.out.println("Updated quiz description: " + result.getDescription());
        assertEquals("Hard", result.getDifficulty());
        System.out.println("Updated quiz difficulty: " + result.getDifficulty());
        verify(quizRepository, times(1)).findById(1L);
        verify(quizRepository, times(1)).save(any(Quiz.class));
        System.out.println("updateQuiz test completed successfully");
    }

    @Test
    void updateQuiz_ShouldThrowException_WhenQuizNotFound() {
        // Arrange
        System.out.println("\n--- TEST: updateQuiz_ShouldThrowException_WhenQuizNotFound ---");
        System.out.println("Setting up mock for non-existent quiz ID: 99");
        when(quizRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling updateQuiz(99L, testQuiz)");
        Exception exception = assertThrows(RuntimeException.class, () -> quizService.updateQuiz(99L, testQuiz));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(quizRepository, times(1)).findById(99L);
        System.out.println("updateQuiz exception test completed successfully");
    }

    @Test
    void deleteQuiz_ShouldDeleteQuizAndDependencies() {
        // Arrange
        System.out.println("\n--- TEST: deleteQuiz_ShouldDeleteQuizAndDependencies ---");
        System.out.println("Setting up mocks for quiz deletion");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.findByQuizId(1L)).thenReturn(Collections.emptyList());
        when(responseRepository.findByQuestionId(1L)).thenReturn(Collections.emptyList());

        // Act
        System.out.println("Calling quizService.deleteQuiz(1L)");
        quizService.deleteQuiz(1L);

        // Assert
        System.out.println("Verifying deletion operations were performed");
        verify(quizAttemptRepository, times(1)).findByQuizId(1L);
        System.out.println("Verified no quiz attempts found");
        verify(responseRepository, atLeastOnce()).findByQuestionId(1L);
        System.out.println("Verified no responses found for questions");
        verify(questionRepository, times(1)).delete(testQuestion);
        System.out.println("Verified question deletion");
        verify(optionRepository, times(1)).delete(testOption);
        System.out.println("Verified option deletion");
        verify(quizRepository, times(1)).delete(testQuiz);
        System.out.println("Verified quiz deletion");
        System.out.println("deleteQuiz test completed successfully");
    }

    @Test
    void createQuizForProfessor_ShouldCreateQuiz_WhenUserIsProfessor() {
        // Arrange
        System.out.println("\n--- TEST: createQuizForProfessor_ShouldCreateQuiz_WhenUserIsProfessor ---");
        System.out.println("Setting up mocks for professor quiz creation");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testProfessor));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);
        when(optionRepository.save(any(Option.class))).thenReturn(testOption);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act
        System.out.println("Calling quizService.createQuizForProfessor(testQuiz, 1L)");
        Quiz createdQuiz = quizService.createQuizForProfessor(testQuiz, 1L);

        // Assert
        System.out.println("Verifying quiz was created for professor");
        assertNotNull(createdQuiz);
        System.out.println("Created quiz title: " + createdQuiz.getTitle());
        assertEquals(testProfessor, createdQuiz.getProfessor());
        System.out.println("Professor role: " + createdQuiz.getProfessor().getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(quizRepository, times(1)).save(testQuiz);
        System.out.println("createQuizForProfessor test completed successfully");
    }

    @Test
    void createQuizForProfessor_ShouldThrowException_WhenUserIsNotProfessor() {
        // Arrange
        System.out.println("\n--- TEST: createQuizForProfessor_ShouldThrowException_WhenUserIsNotProfessor ---");
        System.out.println("Creating student user");
        User student = new User();
        student.setId(2L);
        student.setRole("student");
        System.out.println("Student user role: " + student.getRole());

        System.out.println("Setting up mock for student user ID: 2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling createQuizForProfessor with student ID");
        Exception exception = assertThrows(RuntimeException.class, () -> quizService.createQuizForProfessor(testQuiz, 2L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(userRepository, times(1)).findById(2L);
        System.out.println("createQuizForProfessor exception test completed successfully");
    }

    @Test
    void updateProfessorQuiz_ShouldUpdateQuiz_WhenProfessorIsOwner() {
        // Arrange
        System.out.println("\n--- TEST: updateProfessorQuiz_ShouldUpdateQuiz_WhenProfessorIsOwner ---");
        System.out.println("Creating updated quiz object");
        Quiz updatedQuiz = new Quiz();
        updatedQuiz.setTitle("Updated Quiz");
        System.out.println("Updated quiz title: " + updatedQuiz.getTitle());

        System.out.println("Setting up mocks for professor quiz update");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(updatedQuiz);

        // Act
        System.out.println("Calling quizService.updateProfessorQuiz(1L, updatedQuiz, 1L)");
        Quiz result = quizService.updateProfessorQuiz(1L, updatedQuiz, 1L);

        // Assert
        System.out.println("Verifying quiz was updated");
        assertEquals("Updated Quiz", result.getTitle());
        System.out.println("Updated quiz title: " + result.getTitle());
        verify(quizRepository, times(2)).findById(1L);
        verify(quizRepository, times(1)).save(any(Quiz.class));
        System.out.println("updateProfessorQuiz test completed successfully");
    }

    @Test
    void updateProfessorQuiz_ShouldThrowException_WhenProfessorIsNotOwner() {
        // Arrange
        System.out.println("\n--- TEST: updateProfessorQuiz_ShouldThrowException_WhenProfessorIsNotOwner ---");
        System.out.println("Setting up mock for quiz with owner ID: 1");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling updateProfessorQuiz with different professor ID: 2");
        Exception exception = assertThrows(RuntimeException.class, () -> quizService.updateProfessorQuiz(1L, testQuiz, 2L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(quizRepository, times(1)).findById(1L);
        System.out.println("updateProfessorQuiz exception test completed successfully");
    }

    @Test
    void deleteProfessorQuiz_ShouldDeleteQuiz_WhenProfessorIsOwner() {
        // Arrange
        System.out.println("\n--- TEST: deleteProfessorQuiz_ShouldDeleteQuiz_WhenProfessorIsOwner ---");
        System.out.println("Setting up mocks for professor quiz deletion");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.findByQuizId(1L)).thenReturn(Collections.emptyList());
        when(responseRepository.findByQuestionId(1L)).thenReturn(Collections.emptyList());

        // Act
        System.out.println("Calling quizService.deleteProfessorQuiz(1L, 1L)");
        quizService.deleteProfessorQuiz(1L, 1L);

        // Assert
        System.out.println("Verifying quiz was deleted");
        verify(quizRepository, times(1)).delete(testQuiz);
        System.out.println("deleteProfessorQuiz test completed successfully");
    }

    @Test
    void deleteProfessorQuiz_ShouldThrowException_WhenProfessorIsNotOwner() {
        // Arrange
        System.out.println("\n--- TEST: deleteProfessorQuiz_ShouldThrowException_WhenProfessorIsNotOwner ---");
        System.out.println("Setting up mock for quiz with owner ID: 1");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling deleteProfessorQuiz with different professor ID: 2");
        Exception exception = assertThrows(RuntimeException.class, () -> quizService.deleteProfessorQuiz(1L, 2L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(quizRepository, times(1)).findById(1L);
        System.out.println("deleteProfessorQuiz exception test completed successfully");
    }

    @Test
    void addQuestionToQuiz_ShouldAddQuestion_WhenProfessorIsOwner() {
        // Arrange
        System.out.println("\n--- TEST: addQuestionToQuiz_ShouldAddQuestion_WhenProfessorIsOwner ---");
        System.out.println("Creating new question");
        Question newQuestion = new Question();
        newQuestion.setText("New Question");
        System.out.println("New question text: " + newQuestion.getText());

        System.out.println("Setting up mocks for adding question to quiz");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(questionRepository.save(any(Question.class))).thenReturn(newQuestion);

        // Act
        System.out.println("Calling quizService.addQuestionToQuiz(1L, newQuestion, 1L)");
        Question result = quizService.addQuestionToQuiz(1L, newQuestion, 1L);

        // Assert
        System.out.println("Verifying question was added");
        assertEquals("New Question", result.getText());
        System.out.println("Added question text: " + result.getText());
        verify(questionRepository, times(1)).save(newQuestion);
        System.out.println("addQuestionToQuiz test completed successfully");
    }

    @Test
    void addQuestionToQuiz_ShouldThrowException_WhenProfessorIsNotOwner() {
        // Arrange
        System.out.println("\n--- TEST: addQuestionToQuiz_ShouldThrowException_WhenProfessorIsNotOwner ---");
        System.out.println("Setting up mock for quiz with owner ID: 1");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling addQuestionToQuiz with different professor ID: 2");
        Exception exception = assertThrows(RuntimeException.class, () ->
                quizService.addQuestionToQuiz(1L, testQuestion, 2L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(quizRepository, times(1)).findById(1L);
        System.out.println("addQuestionToQuiz exception test completed successfully");
    }

    @Test
    void updateQuestion_ShouldUpdateQuestion_WhenProfessorIsOwner() {
        // Arrange
        System.out.println("\n--- TEST: updateQuestion_ShouldUpdateQuestion_WhenProfessorIsOwner ---");
        System.out.println("Creating updated question");
        Question updatedQuestion = new Question();
        updatedQuestion.setText("Updated Question");
        updatedQuestion.setOptions(Collections.singletonList(testOption));
        System.out.println("Updated question text: " + updatedQuestion.getText());

        System.out.println("Setting up mocks for question update");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(updatedQuestion);
        when(responseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        System.out.println("Calling quizService.updateQuestion(1L, updatedQuestion, 1L)");
        Question result = quizService.updateQuestion(1L, updatedQuestion, 1L);

        // Assert
        System.out.println("Verifying question was updated");
        assertEquals("Updated Question", result.getText());
        System.out.println("Updated question text: " + result.getText());
        verify(questionRepository, times(1)).save(any(Question.class));
        System.out.println("updateQuestion test completed successfully");
    }

    @Test
    void updateQuestion_ShouldThrowException_WhenProfessorIsNotOwner() {
        // Arrange
        System.out.println("\n--- TEST: updateQuestion_ShouldThrowException_WhenProfessorIsNotOwner ---");
        System.out.println("Setting up mock for question with quiz owner ID: 1");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling updateQuestion with different professor ID: 2");
        Exception exception = assertThrows(RuntimeException.class, () ->
                quizService.updateQuestion(1L, testQuestion, 2L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(questionRepository, times(1)).findById(1L);
        System.out.println("updateQuestion exception test completed successfully");
    }

    @Test
    void deleteQuestion_ShouldDeleteQuestion_WhenProfessorIsOwner() {
        // Arrange
        System.out.println("\n--- TEST: deleteQuestion_ShouldDeleteQuestion_WhenProfessorIsOwner ---");
        System.out.println("Setting up mocks for question deletion");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(responseRepository.findByQuestionId(1L)).thenReturn(Collections.emptyList());

        // Act
        System.out.println("Calling quizService.deleteQuestion(1L, 1L)");
        quizService.deleteQuestion(1L, 1L);

        // Assert
        System.out.println("Verifying question was deleted");
        verify(questionRepository, times(1)).delete(testQuestion);
        System.out.println("deleteQuestion test completed successfully");
    }

    @Test
    void deleteQuestion_ShouldThrowException_WhenProfessorIsNotOwner() {
        // Arrange
        System.out.println("\n--- TEST: deleteQuestion_ShouldThrowException_WhenProfessorIsNotOwner ---");
        System.out.println("Setting up mock for question with quiz owner ID: 1");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling deleteQuestion with different professor ID: 2");
        Exception exception = assertThrows(RuntimeException.class, () -> quizService.deleteQuestion(1L, 2L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(questionRepository, times(1)).findById(1L);
        System.out.println("deleteQuestion exception test completed successfully");
    }
}
