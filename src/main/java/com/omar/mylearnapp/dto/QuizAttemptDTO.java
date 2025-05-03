package com.omar.mylearnapp.dto;

import com.omar.mylearnapp.model.QuizAttempt;
import java.time.Duration;
import java.time.LocalDateTime;

public class QuizAttemptDTO {

    private Long id;
    private Long userId;
    private String userName;
    private Long quizId;
    private String quizTitle;
    private String quizCategory; // Mapped from topic name
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int score;
    private int totalQuestions;
    private Long timeTakenSeconds;

    public QuizAttemptDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizCategory() {
        return quizCategory;
    }

    public void setQuizCategory(String quizCategory) {
        this.quizCategory = quizCategory;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Long getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(Long timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    // Factory method
    public static QuizAttemptDTO fromQuizAttempt(QuizAttempt attempt) {
        QuizAttemptDTO dto = new QuizAttemptDTO();

        dto.setId(attempt.getId());

        if (attempt.getUser() != null) {
            dto.setUserId(attempt.getUser().getId());

            String firstName = attempt.getUser().getFirstName() != null ? attempt.getUser().getFirstName() : "";
            String lastName = attempt.getUser().getLastName() != null ? attempt.getUser().getLastName() : "";
            dto.setUserName((firstName + " " + lastName).trim());
        }

        if (attempt.getQuiz() != null) {
            dto.setQuizId(attempt.getQuiz().getId());

            dto.setQuizTitle(
                    attempt.getQuiz().getTitle() != null ?
                            attempt.getQuiz().getTitle() :
                            "Untitled Quiz"
            );

            dto.setQuizCategory(
                    attempt.getQuiz().getTopic() != null &&
                            attempt.getQuiz().getTopic().getName() != null ?
                            attempt.getQuiz().getTopic().getName() :
                            "Uncategorized"
            );

            dto.setTotalQuestions(
                    attempt.getQuiz().getQuestions() != null ?
                            attempt.getQuiz().getQuestions().size() :
                            0
            );
        }

        dto.setStartTime(attempt.getStartTime());
        dto.setEndTime(attempt.getEndTime());
        dto.setScore(attempt.getScore());

        // Fallback if timeTakenSeconds isn't calculated in the entity
        if (attempt.getTimeTakenSeconds() != null) {
            dto.setTimeTakenSeconds(attempt.getTimeTakenSeconds());
        } else if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
            long seconds = Duration.between(attempt.getStartTime(), attempt.getEndTime()).getSeconds();
            dto.setTimeTakenSeconds(seconds);
        } else {
            dto.setTimeTakenSeconds(0L);
        }

        return dto;
    }
}
