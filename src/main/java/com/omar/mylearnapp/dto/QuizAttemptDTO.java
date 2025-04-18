package com.omar.mylearnapp.dto;

import com.omar.mylearnapp.model.QuizAttempt;

import java.time.LocalDateTime;

public class QuizAttemptDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long quizId;
    private String quizTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int score;
    private int totalQuestions;
    private Long timeTakenSeconds;


    public QuizAttemptDTO() {
    }

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


    public static QuizAttemptDTO fromQuizAttempt(QuizAttempt attempt) {
        QuizAttemptDTO dto = new QuizAttemptDTO();
        dto.setId(attempt.getId());
        dto.setUserId(attempt.getUser().getId());
        dto.setUserName(attempt.getUser().getFirstName() + " " + attempt.getUser().getLastName());
        dto.setQuizId(attempt.getQuiz().getId());
        dto.setQuizTitle(attempt.getQuiz().getTitle());
        dto.setStartTime(attempt.getStartTime());
        dto.setEndTime(attempt.getEndTime());
        dto.setScore(attempt.getScore());
        dto.setTotalQuestions(attempt.getTotalQuestions());
        dto.setTimeTakenSeconds(attempt.getTimeTakenSeconds());
        return dto;
    }
}
