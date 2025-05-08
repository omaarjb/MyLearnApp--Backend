package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.Topic;
import com.omar.mylearnapp.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicService topicService;

    private Topic testTopic;
    private Topic testTopic2;
    private List<Topic> topicList;

    @BeforeEach
    void setUp() {
        System.out.println("\n--- Setting up test environment ---");

        testTopic = new Topic();
        testTopic.setId(1L);
        testTopic.setName("Java");
        testTopic.setDescription("Programming language");
        System.out.println("Test topic created with ID: " + testTopic.getId() + ", Name: " + testTopic.getName());

        testTopic2 = new Topic();
        testTopic2.setId(2L);
        testTopic2.setName("Spring");
        testTopic2.setDescription("Framework for Java");
        System.out.println("Test topic 2 created with ID: " + testTopic2.getId() + ", Name: " + testTopic2.getName());

        topicList = Arrays.asList(testTopic, testTopic2);
        System.out.println("Topic list created with " + topicList.size() + " topics");

        System.out.println("Test setup completed successfully");
    }

    @Test
    void getAllTopics_ShouldReturnAllTopics() {
        // Arrange
        System.out.println("\n--- TEST: getAllTopics_ShouldReturnAllTopics ---");
        System.out.println("Setting up mock for findAll()");
        when(topicRepository.findAll()).thenReturn(topicList);

        // Act
        System.out.println("Calling topicService.getAllTopics()");
        List<Topic> result = topicService.getAllTopics();

        // Assert
        System.out.println("Verifying all topics were returned");
        assertEquals(2, result.size());
        assertEquals(testTopic, result.get(0));
        assertEquals(testTopic2, result.get(1));
        verify(topicRepository, times(1)).findAll();
        System.out.println("getAllTopics test completed successfully");
    }

    @Test
    void getTopicById_ShouldReturnTopic_WhenExists() {
        // Arrange
        System.out.println("\n--- TEST: getTopicById_ShouldReturnTopic_WhenExists ---");
        System.out.println("Setting up mock for findById(1L)");
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // Act
        System.out.println("Calling topicService.getTopicById(1L)");
        Optional<Topic> result = topicService.getTopicById(1L);

        // Assert
        System.out.println("Verifying topic was returned");
        assertTrue(result.isPresent());
        assertEquals(testTopic, result.get());
        verify(topicRepository, times(1)).findById(1L);
        System.out.println("getTopicById test completed successfully");
    }

    @Test
    void getTopicById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        System.out.println("\n--- TEST: getTopicById_ShouldReturnEmpty_WhenNotExists ---");
        System.out.println("Setting up mock for findById(99L)");
        when(topicRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        System.out.println("Calling topicService.getTopicById(99L)");
        Optional<Topic> result = topicService.getTopicById(99L);

        // Assert
        System.out.println("Verifying empty optional was returned");
        assertFalse(result.isPresent());
        verify(topicRepository, times(1)).findById(99L);
        System.out.println("getTopicById empty test completed successfully");
    }

    @Test
    void getTopicByName_ShouldReturnTopic_WhenExists() {
        // Arrange
        System.out.println("\n--- TEST: getTopicByName_ShouldReturnTopic_WhenExists ---");
        System.out.println("Setting up mock for findByName('Java')");
        when(topicRepository.findByName("Java")).thenReturn(Optional.of(testTopic));

        // Act
        System.out.println("Calling topicService.getTopicByName('Java')");
        Optional<Topic> result = topicService.getTopicByName("Java");

        // Assert
        System.out.println("Verifying topic was returned");
        assertTrue(result.isPresent());
        assertEquals(testTopic, result.get());
        verify(topicRepository, times(1)).findByName("Java");
        System.out.println("getTopicByName test completed successfully");
    }

    @Test
    void getTopicByName_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        System.out.println("\n--- TEST: getTopicByName_ShouldReturnEmpty_WhenNotExists ---");
        System.out.println("Setting up mock for findByName('Unknown')");
        when(topicRepository.findByName("Unknown")).thenReturn(Optional.empty());

        // Act
        System.out.println("Calling topicService.getTopicByName('Unknown')");
        Optional<Topic> result = topicService.getTopicByName("Unknown");

        // Assert
        System.out.println("Verifying empty optional was returned");
        assertFalse(result.isPresent());
        verify(topicRepository, times(1)).findByName("Unknown");
        System.out.println("getTopicByName empty test completed successfully");
    }

    @Test
    void searchTopic_ShouldReturnMatchingTopics() {
        // Arrange
        System.out.println("\n--- TEST: searchTopic_ShouldReturnMatchingTopics ---");
        System.out.println("Setting up mock for findByNameContainingIgnoreCase('Java')");
        when(topicRepository.findByNameContainingIgnoreCase("Java")).thenReturn(Arrays.asList(testTopic));

        // Act
        System.out.println("Calling topicService.searchTopic('Java')");
        List<Topic> result = topicService.searchTopic("Java");

        // Assert
        System.out.println("Verifying matching topics were returned");
        assertEquals(1, result.size());
        assertEquals(testTopic, result.get(0));
        verify(topicRepository, times(1)).findByNameContainingIgnoreCase("Java");
        System.out.println("searchTopic test completed successfully");
    }

    @Test
    void createTopic_ShouldCreateAndReturnTopic() {
        // Arrange
        System.out.println("\n--- TEST: createTopic_ShouldCreateAndReturnTopic ---");
        System.out.println("Setting up mock for save()");
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        System.out.println("Calling topicService.createTopic(testTopic)");
        Topic result = topicService.createTopic(testTopic);

        // Assert
        System.out.println("Verifying topic was created");
        assertNotNull(result);
        assertEquals(testTopic, result);
        verify(topicRepository, times(1)).save(testTopic);
        System.out.println("createTopic test completed successfully");
    }

    @Test
    void updateTopic_ShouldUpdateAndReturnTopic_WhenExists() {
        // Arrange
        System.out.println("\n--- TEST: updateTopic_ShouldUpdateAndReturnTopic_WhenExists ---");
        Topic updatedTopic = new Topic();
        updatedTopic.setId(1L);
        updatedTopic.setName("Updated Java");
        updatedTopic.setDescription("Updated Description");
        System.out.println("Created updated topic with name: " + updatedTopic.getName());

        System.out.println("Setting up mocks for findById() and save()");
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(updatedTopic);

        // Act
        System.out.println("Calling topicService.updateTopic(1L, updatedTopic)");
        Topic result = topicService.updateTopic(1L, updatedTopic);

        // Assert
        System.out.println("Verifying topic was updated");
        assertNotNull(result);
        assertEquals(updatedTopic, result);
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).save(any(Topic.class));
        System.out.println("updateTopic test completed successfully");
    }

    @Test
    void updateTopic_ShouldThrowException_WhenNotExists() {
        // Arrange
        System.out.println("\n--- TEST: updateTopic_ShouldThrowException_WhenNotExists ---");
        System.out.println("Setting up mock for findById(99L)");
        when(topicRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling updateTopic with non-existent ID");
        Exception exception = assertThrows(RuntimeException.class, () ->
                topicService.updateTopic(99L, testTopic));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(topicRepository, times(1)).findById(99L);
        System.out.println("updateTopic exception test completed successfully");
    }

    @Test
    void deleteTopic_ShouldDeleteTopic_WhenExists() {
        // Arrange
        System.out.println("\n--- TEST: deleteTopic_ShouldDeleteTopic_WhenExists ---");
        System.out.println("Setting up mock for findById(1L)");
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        doNothing().when(topicRepository).delete(testTopic);

        // Act
        System.out.println("Calling topicService.deleteTopic(1L)");
        topicService.deleteTopic(1L);

        // Assert
        System.out.println("Verifying topic was deleted");
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).delete(testTopic);
        System.out.println("deleteTopic test completed successfully");
    }

    @Test
    void deleteTopic_ShouldThrowException_WhenNotExists() {
        // Arrange
        System.out.println("\n--- TEST: deleteTopic_ShouldThrowException_WhenNotExists ---");
        System.out.println("Setting up mock for findById(99L)");
        when(topicRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        System.out.println("Expecting RuntimeException when calling deleteTopic with non-existent ID");
        Exception exception = assertThrows(RuntimeException.class, () ->
                topicService.deleteTopic(99L));
        System.out.println("Exception thrown as expected: " + exception.getMessage());
        verify(topicRepository, times(1)).findById(99L);
        System.out.println("deleteTopic exception test completed successfully");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenExists() {
        // Arrange
        System.out.println("\n--- TEST: existsById_ShouldReturnTrue_WhenExists ---");
        System.out.println("Setting up mock for existsById(1L)");
        when(topicRepository.existsById(1L)).thenReturn(true);

        // Act
        System.out.println("Calling topicService.existsById(1L)");
        boolean result = topicService.existsById(1L);

        // Assert
        System.out.println("Verifying result is true");
        assertTrue(result);
        verify(topicRepository, times(1)).existsById(1L);
        System.out.println("existsById true test completed successfully");
    }

    @Test
    void existsById_ShouldReturnFalse_WhenNotExists() {
        // Arrange
        System.out.println("\n--- TEST: existsById_ShouldReturnFalse_WhenNotExists ---");
        System.out.println("Setting up mock for existsById(99L)");
        when(topicRepository.existsById(99L)).thenReturn(false);

        // Act
        System.out.println("Calling topicService.existsById(99L)");
        boolean result = topicService.existsById(99L);

        // Assert
        System.out.println("Verifying result is false");
        assertFalse(result);
        verify(topicRepository, times(1)).existsById(99L);
        System.out.println("existsById false test completed successfully");
    }

    @Test
    void existsByName_ShouldReturnTrue_WhenExists() {
        // Arrange
        System.out.println("\n--- TEST: existsByName_ShouldReturnTrue_WhenExists ---");
        System.out.println("Setting up mock for findByName('Java')");
        when(topicRepository.findByName("Java")).thenReturn(Optional.of(testTopic));

        // Act
        System.out.println("Calling topicService.existsByName('Java')");
        boolean result = topicService.existsByName("Java");

        // Assert
        System.out.println("Verifying result is true");
        assertTrue(result);
        verify(topicRepository, times(1)).findByName("Java");
        System.out.println("existsByName true test completed successfully");
    }

    @Test
    void existsByName_ShouldReturnFalse_WhenNotExists() {
        // Arrange
        System.out.println("\n--- TEST: existsByName_ShouldReturnFalse_WhenNotExists ---");
        System.out.println("Setting up mock for findByName('Unknown')");
        when(topicRepository.findByName("Unknown")).thenReturn(Optional.empty());

        // Act
        System.out.println("Calling topicService.existsByName('Unknown')");
        boolean result = topicService.existsByName("Unknown");

        // Assert
        System.out.println("Verifying result is false");
        assertFalse(result);
        verify(topicRepository, times(1)).findByName("Unknown");
        System.out.println("existsByName false test completed successfully");
    }

    @Test
    void createMultipleTopics_ShouldCreateAndReturnTopics() {
        // Arrange
        System.out.println("\n--- TEST: createMultipleTopics_ShouldCreateAndReturnTopics ---");
        System.out.println("Setting up mock for saveAll()");
        when(topicRepository.saveAll(anyList())).thenReturn(topicList);

        // Act
        System.out.println("Calling topicService.createMultipleTopics(topicList)");
        List<Topic> result = topicService.createMultipleTopics(topicList);

        // Assert
        System.out.println("Verifying multiple topics were created");
        assertEquals(2, result.size());
        assertEquals(testTopic, result.get(0));
        assertEquals(testTopic2, result.get(1));
        verify(topicRepository, times(1)).saveAll(topicList);
        System.out.println("createMultipleTopics test completed successfully");
    }
}