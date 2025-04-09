package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.*;
import com.omar.mylearnapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
    public QuizAttempt startQuizAttempt(Long userId,Long quizId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found with id: " +userId));
        Quiz quiz=quizRepository.findById(quizId)
                .orElseThrow(()->new RuntimeException("Quiz not found with id: " +quizId));
        QuizAttempt attempt=new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setTotalQuestions(quiz.getQuestions()!= null ? quiz.getQuestions().size() : 0);

        return quizAttemptRepository.save(attempt);

    }

    @Transactional
    public QuizAttempt submitQuizAttempt(Long attemptId, Map<Long,Long> responses){
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(()->new RuntimeException("Quiz attempt not found with id: " +attemptId));
        attempt.setEndTime(LocalDateTime.now());

        LocalDateTime now= LocalDateTime.now();
        attempt.setEndTime(now);

        long secondsTaken = ChronoUnit.SECONDS.between(attempt.getStartTime(),attempt.getEndTime());
        attempt.setTimeTakenSeconds(secondsTaken);

        Quiz quiz= attempt.getQuiz();
        boolean timeLimitExceeded = false;

        if (quiz.getTimeLimit() != null && secondsTaken > quiz.getTimeLimit()) {
            timeLimitExceeded = true;
        }

        List<Response> responseList=new ArrayList<>();
        int correctAnswers=0;

        if(!timeLimitExceeded) {
            for(Map.Entry<Long,Long> entry: responses.entrySet()) {
                Long questionId = entry.getKey();
                Long selectedOptionId = entry.getValue();

                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
                Option selectedOption = optionRepository.findById(selectedOptionId)
                        .orElseThrow(() -> new RuntimeException("Option not found with id: " + selectedOptionId));

                Response response = new Response();
                response.setQuizAttempt(attempt);
                response.setQuestion(question);
                response.setSelectedOption(selectedOption);
                response.setCorrect(selectedOption.isCorrect());

                responseList.add(responseRepository.save(response));

                if (selectedOption.isCorrect()) {
                    correctAnswers++;
                }
            }
        }


            attempt.setScore(correctAnswers);
            attempt.setResponses(responseList);

            return quizAttemptRepository.save(attempt);

        }

    public boolean hasTimeLimitExceeded(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found with id: " + attemptId));

        Quiz quiz = attempt.getQuiz();
        if (quiz.getTimeLimit() == null) {
            return false; // No time limit set
        }

        LocalDateTime now = LocalDateTime.now();
        long secondsTaken = ChronoUnit.SECONDS.between(attempt.getStartTime(), now);

        return secondsTaken > quiz.getTimeLimit();
    }


    @Transactional
    public QuizAttempt autoSubmitExpiredAttempt(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found with id: " + attemptId));

        LocalDateTime now = LocalDateTime.now();
        attempt.setEndTime(now);

        // Calculate time taken
        long secondsTaken = ChronoUnit.SECONDS.between(attempt.getStartTime(), now);
        attempt.setTimeTakenSeconds(secondsTaken);

        // Set score to 0 as time limit was exceeded
        attempt.setScore(0);

        return quizAttemptRepository.save(attempt);
    }


        // Attempts dyal user
        public List<QuizAttempt> getUserAttempts(Long userId){
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new RuntimeException("User not found with id: " +userId));
            return quizAttemptRepository.findByUserId(userId);
        }


        // Recent attempts dyal user
        public List<QuizAttempt> getRecentUserAttempts(Long userId){
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new RuntimeException("User not found with id: " +userId));
            return quizAttemptRepository.findRecentAttemptsByUserId(userId);
        }


        // Attempts dyal quiz
        public List<QuizAttempt> getQuizAttempts(Long quizId){
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(()->new RuntimeException("Quiz not found with id: " +quizId));
            return quizAttemptRepository.findByQuizId(quizId);
        }


        // Attempt by id
        public Optional<QuizAttempt> getAttemptById(Long attemptId){
            return quizAttemptRepository.findById(attemptId);
        }


        // Attempts dyal user f wahd lquiz
        public List<QuizAttempt> getUserAttemptsForQuiz(Long userId, Long quizId){
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new RuntimeException("User not found with id: " +userId));
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(()->new RuntimeException("Quiz not found with id: " +quizId));
            return quizAttemptRepository.findByUserIdAndQuizId(userId,quizId);
        }



        public List<Response> getResponsesForAttempt(Long attemptId){
            QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                    .orElseThrow(()->new RuntimeException("Quiz attempt not found with id: " +attemptId));
            return responseRepository.findByQuizAttemptId(attemptId);
        }

        public Response getResponseForQuestion(Long attemptId,Long questionId){
            QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                    .orElseThrow(()->new RuntimeException("Quiz attempt not found with id: " +attemptId));
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(()->new RuntimeException("Question not found with id: " +questionId));
            return responseRepository.findByAttemptIdAndQuestionId(attemptId,questionId);
        }

        @Transactional
    public void deleteQuizAttempt(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found with id: " + attemptId));
        List<Response> responses = responseRepository.findByQuizAttemptId(attemptId);
        responseRepository.deleteAll(responses);
        quizAttemptRepository.deleteById(attemptId);
    }

}
