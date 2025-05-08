package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.User;
import com.omar.mylearnapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private String testClerkId;
    private String testEmail;
    private String testRole;
    private String testFirstName;
    private String testLastName;

    @BeforeEach
    void setUp() {
        System.out.println("\n--- Setting up test environment ---");

        testClerkId = "user_123";
        testEmail = "test@example.com";
        testRole = "student";
        testFirstName = "John";
        testLastName = "Doe";

        testUser = new User();
        testUser.setId(1L);
        testUser.setClerkId(testClerkId);
        testUser.setEmail(testEmail);
        testUser.setRole(testRole);
        testUser.setFirstName(testFirstName);
        testUser.setLastName(testLastName);

        System.out.println("Test user created with ID: " + testUser.getId() +
                ", ClerkId: " + testUser.getClerkId() +
                ", Email: " + testUser.getEmail() +
                ", Role: " + testUser.getRole() +
                ", Name: " + testUser.getFirstName() + " " + testUser.getLastName());

        System.out.println("Test setup completed successfully");
    }

    @Test
    void createUser_ShouldCreateNewUser_WhenUserDoesNotExist() {
        // Arrange
        System.out.println("\n--- TEST: createUser_ShouldCreateNewUser_WhenUserDoesNotExist ---");
        System.out.println("Setting up mock for existsByClerkId('" + testClerkId + "') to return false");
        when(userRepository.existsByClerkId(testClerkId)).thenReturn(false);
        System.out.println("Setting up mock for save()");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        System.out.println("Calling userService.createUser() with test data");
        userService.createUser(testClerkId, testEmail, testRole, testFirstName, testLastName);

        // Assert
        System.out.println("Verifying userRepository.existsByClerkId() was called");
        verify(userRepository, times(1)).existsByClerkId(testClerkId);

        System.out.println("Verifying userRepository.save() was called with the correct user data");
        verify(userRepository, times(1)).save(argThat(user ->
                user.getClerkId().equals(testClerkId) &&
                        user.getEmail().equals(testEmail) &&
                        user.getRole().equals(testRole) &&
                        user.getFirstName().equals(testFirstName) &&
                        user.getLastName().equals(testLastName)
        ));

        System.out.println("createUser test completed successfully");
    }

    @Test
    void createUser_ShouldNotCreateUser_WhenUserAlreadyExists() {
        // Arrange
        System.out.println("\n--- TEST: createUser_ShouldNotCreateUser_WhenUserAlreadyExists ---");
        System.out.println("Setting up mock for existsByClerkId('" + testClerkId + "') to return true");
        when(userRepository.existsByClerkId(testClerkId)).thenReturn(true);

        // Act
        System.out.println("Calling userService.createUser() with test data");
        userService.createUser(testClerkId, testEmail, testRole, testFirstName, testLastName);

        // Assert
        System.out.println("Verifying userRepository.existsByClerkId() was called");
        verify(userRepository, times(1)).existsByClerkId(testClerkId);

        System.out.println("Verifying userRepository.save() was NOT called");
        verify(userRepository, never()).save(any(User.class));

        System.out.println("createUser existing user test completed successfully");
    }

    @Test
    void updateUserRole_ShouldUpdateRole_WhenUserExists() {
        // Arrange
        System.out.println("\n--- TEST: updateUserRole_ShouldUpdateRole_WhenUserExists ---");
        String newRole = "teacher";
        System.out.println("Setting up new role: " + newRole);

        System.out.println("Setting up mock for findByClerkId('" + testClerkId + "')");
        when(userRepository.findByClerkId(testClerkId)).thenReturn(Optional.of(testUser));

        System.out.println("Setting up mock for save()");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        System.out.println("Calling userService.updateUserRole('" + testClerkId + "', '" + newRole + "')");
        boolean result = userService.updateUserRole(testClerkId, newRole);

        // Assert
        System.out.println("Verifying result is true");
        assertTrue(result);

        System.out.println("Verifying user role was updated to: " + newRole);
        verify(userRepository, times(1)).findByClerkId(testClerkId);
        verify(userRepository, times(1)).save(argThat(user -> user.getRole().equals(newRole)));

        System.out.println("updateUserRole test completed successfully");
    }

    @Test
    void updateUserRole_ShouldReturnFalse_WhenUserDoesNotExist() {
        // Arrange
        System.out.println("\n--- TEST: updateUserRole_ShouldReturnFalse_WhenUserDoesNotExist ---");
        String newRole = "teacher";
        String nonExistentClerkId = "unknown_user";
        System.out.println("Setting up new role: " + newRole + " for non-existent user: " + nonExistentClerkId);

        System.out.println("Setting up mock for findByClerkId('" + nonExistentClerkId + "') to return empty");
        when(userRepository.findByClerkId(nonExistentClerkId)).thenReturn(Optional.empty());

        // Act
        System.out.println("Calling userService.updateUserRole('" + nonExistentClerkId + "', '" + newRole + "')");
        boolean result = userService.updateUserRole(nonExistentClerkId, newRole);

        // Assert
        System.out.println("Verifying result is false");
        assertFalse(result);

        System.out.println("Verifying findByClerkId was called");
        verify(userRepository, times(1)).findByClerkId(nonExistentClerkId);

        System.out.println("Verifying save was NOT called");
        verify(userRepository, never()).save(any(User.class));

        System.out.println("updateUserRole non-existent user test completed successfully");
    }

    @Test
    void deleteUserByClerkId_ShouldDeleteUser() {
        // Arrange
        System.out.println("\n--- TEST: deleteUserByClerkId_ShouldDeleteUser ---");
        System.out.println("Setting up test for deletion of user with clerkId: " + testClerkId);
        doNothing().when(userRepository).deleteByClerkId(testClerkId);

        // Act
        System.out.println("Calling userService.deleteUserByClerkId('" + testClerkId + "')");
        userService.deleteUserByClerkId(testClerkId);

        // Assert
        System.out.println("Verifying userRepository.deleteByClerkId() was called");
        verify(userRepository, times(1)).deleteByClerkId(testClerkId);

        System.out.println("deleteUserByClerkId test completed successfully");
    }

    @Test
    void findByClerkId_ShouldReturnUser_WhenUserExists() {
        // Arrange
        System.out.println("\n--- TEST: findByClerkId_ShouldReturnUser_WhenUserExists ---");
        System.out.println("Setting up mock for findByClerkId('" + testClerkId + "')");
        when(userRepository.findByClerkId(testClerkId)).thenReturn(Optional.of(testUser));

        // Act
        System.out.println("Calling userService.findByClerkId('" + testClerkId + "')");
        Optional<User> result = userService.findByClerkId(testClerkId);

        // Assert
        System.out.println("Verifying user was found");
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());

        System.out.println("Verifying userRepository.findByClerkId() was called");
        verify(userRepository, times(1)).findByClerkId(testClerkId);

        System.out.println("findByClerkId test completed successfully");
    }

    @Test
    void findByClerkId_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        System.out.println("\n--- TEST: findByClerkId_ShouldReturnEmpty_WhenUserDoesNotExist ---");
        String nonExistentClerkId = "unknown_user";
        System.out.println("Setting up mock for findByClerkId('" + nonExistentClerkId + "') to return empty");
        when(userRepository.findByClerkId(nonExistentClerkId)).thenReturn(Optional.empty());

        // Act
        System.out.println("Calling userService.findByClerkId('" + nonExistentClerkId + "')");
        Optional<User> result = userService.findByClerkId(nonExistentClerkId);

        // Assert
        System.out.println("Verifying empty optional was returned");
        assertFalse(result.isPresent());

        System.out.println("Verifying userRepository.findByClerkId() was called");
        verify(userRepository, times(1)).findByClerkId(nonExistentClerkId);

        System.out.println("findByClerkId empty test completed successfully");
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        System.out.println("\n--- TEST: findById_ShouldReturnUser_WhenUserExists ---");
        Long userId = 1L;
        System.out.println("Setting up mock for findById(" + userId + ")");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        System.out.println("Calling userService.findById(" + userId + ")");
        Optional<User> result = userService.findById(userId);

        // Assert
        System.out.println("Verifying user was found");
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());

        System.out.println("Verifying userRepository.findById() was called");
        verify(userRepository, times(1)).findById(userId);

        System.out.println("findById test completed successfully");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        System.out.println("\n--- TEST: findById_ShouldReturnEmpty_WhenUserDoesNotExist ---");
        Long nonExistentId = 99L;
        System.out.println("Setting up mock for findById(" + nonExistentId + ") to return empty");
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        System.out.println("Calling userService.findById(" + nonExistentId + ")");
        Optional<User> result = userService.findById(nonExistentId);

        // Assert
        System.out.println("Verifying empty optional was returned");
        assertFalse(result.isPresent());

        System.out.println("Verifying userRepository.findById() was called");
        verify(userRepository, times(1)).findById(nonExistentId);

        System.out.println("findById empty test completed successfully");
    }
}