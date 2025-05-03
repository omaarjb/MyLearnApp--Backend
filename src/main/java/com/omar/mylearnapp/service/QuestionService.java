package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.Option;
import com.omar.mylearnapp.model.Question;
import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.repository.OptionRepository;
import com.omar.mylearnapp.repository.QuestionRepository;
import com.omar.mylearnapp.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuizRepository quizRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    @Transactional
    public Question createCompleteQuestion(Question question) {
        // Attach quiz
        if (question.getQuiz() != null && question.getQuiz().getId() != null) {
            Quiz quiz = quizRepository.findById(question.getQuiz().getId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + question.getQuiz().getId()));
            question.setQuiz(quiz);
        }

        // Save question
        Question savedQuestion = questionRepository.save(question);

        // Save options if available
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            for (Option option : question.getOptions()) {
                option.setQuestion(savedQuestion);
                optionRepository.save(option);
            }
        }

        return savedQuestion;
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
