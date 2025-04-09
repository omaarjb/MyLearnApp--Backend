package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.model.response.QuizResponse;
import com.omar.mylearnapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody Quiz quiz){
        Quiz createdQuiz = quizService.createQuiz(quiz);
        return new ResponseEntity<>(QuizResponse.fromQuiz(createdQuiz), HttpStatus.CREATED);
    }

    @PostMapping("/complete")
    public ResponseEntity<QuizResponse> createCompleteQuiz(@RequestBody Quiz quiz){
        Quiz createdQuiz = quizService.createCompleteQuiz(quiz);
        return new ResponseEntity<>(QuizResponse.fromQuiz(createdQuiz), HttpStatus.CREATED);
    }
}
