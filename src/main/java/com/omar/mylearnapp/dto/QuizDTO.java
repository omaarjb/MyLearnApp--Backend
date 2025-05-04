package com.omar.mylearnapp.dto;

import com.omar.mylearnapp.model.Question;
import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.model.User;

import java.util.List;

public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String icon;
    private String color;
    private int timeLimit;
    private List<Question> questions;
    private User professor;
    private TopicDTO topic;

    public QuizDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public User getProfessor() {
        return professor;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setProfessor(User professor) {
        this.professor = professor;
    }
    // Add getter for topic
    public TopicDTO getTopic() {
        return topic;
    }


    public void setTopic(TopicDTO topic) {
        this.topic = topic;
    }
    public static QuizDTO fromQuiz(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setCategory(quiz.getCategory());
        dto.setDifficulty(quiz.getDifficulty());
        dto.setIcon(quiz.getIcon());
        dto.setColor(quiz.getColor());
        dto.setTimeLimit(quiz.getTimeLimit());
        dto.setQuestions(quiz.getQuestions());
        dto.setProfessor(quiz.getProfessor());

        // Set the topic if it exists
        if (quiz.getTopic() != null) {
            dto.setTopic(TopicDTO.fromTopic(quiz.getTopic()));
        }

        return dto;
    }


}
