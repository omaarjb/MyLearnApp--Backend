package com.omar.mylearnapp.model.response;

import com.omar.mylearnapp.model.Option;
import com.omar.mylearnapp.model.Question;
import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String icon;
    private String color;
    private int timeLimit;
    private List<QuestionResponse> questions;
    private ProfessorInfo professor;

    public static QuizResponse fromQuiz(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setCategory(quiz.getCategory());
        response.setDifficulty(quiz.getDifficulty());
        response.setIcon(quiz.getIcon());
        response.setColor(quiz.getColor());
        response.setTimeLimit(quiz.getTimeLimit());

        if (quiz.getQuestions() != null) {
            response.setQuestions(quiz.getQuestions().stream()
                    .map(QuestionResponse::fromQuestion)
                    .collect(Collectors.toList()));
        }

        if (quiz.getProfessor() != null) {
            User prof = quiz.getProfessor();
            ProfessorInfo professorInfo = new ProfessorInfo();
            professorInfo.setId(prof.getId());
            professorInfo.setName(prof.getFirstName() + " " + prof.getLastName());
            professorInfo.setEmail(prof.getEmail());
            response.setProfessor(professorInfo);
        }

        return response;
    }

    // Getters and setters
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

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public ProfessorInfo
    getProfessor() {
        return professor;
    }

    public void
    setProfessor(ProfessorInfo professor) {
        this.professor = professor;
    }

    public static class
    ProfessorInfo
    {
        private Long
                id;
        private String
                name;
        private String
                email;

        // Getters and setters
        public Long
        getId() {
        return id;
    }

        public void
        setId(Long id) {
        this.id = id;
    }

        public String
        getName() {
        return name;
    }

        public void
        setName(String name) {
        this.name = name;
    }

        public String
        getEmail() {
        return email;
    }

        public void
        setEmail(String email) {
        this.email = email;
    }
    }

    public static class QuestionResponse {
        private Long id;
        private String text;
        private List<OptionResponse> options;
        private Long correctOptionId;

        public static QuestionResponse fromQuestion(Question question) {
            QuestionResponse response = new QuestionResponse();
            response.setId(question.getId());
            response.setText(question.getText());

            if (question.getOptions() != null) {
                response.setOptions(question.getOptions().stream()
                        .map(OptionResponse::fromOption)
                        .collect(Collectors.toList()));

                // Find the correct option
                question.getOptions().stream()
                        .filter(Option::isCorrect)
                        .findFirst()
                        .ifPresent(option -> response.setCorrectOptionId(option.getId()));
            }

            return response;
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<OptionResponse> getOptions() {
            return options;
        }

        public void setOptions(List<OptionResponse> options) {
            this.options = options;
        }

        public Long getCorrectOptionId() {
            return correctOptionId;
        }

        public void setCorrectOptionId(Long correctOptionId) {
            this.correctOptionId = correctOptionId;
        }
    }

    public static class OptionResponse {
        private Long id;
        private String text;

        public static OptionResponse fromOption(Option option) {
            OptionResponse response = new OptionResponse();
            response.setId(option.getId());
            response.setText(option.getText());
            return response;
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}