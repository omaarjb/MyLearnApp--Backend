package com.omar.mylearnapp.repository;

import com.omar.mylearnapp.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByQuizAttemptId(Long quizAttemptId);
    List<Response> findByQuestionId(Long questionId);

    @Query("SELECT r FROM Response r WHERE r.quizAttempt.id = :attemptId AND r.question.id = :questionId")
    Response findByAttemptIdAndQuestionId(Long attemptId, Long questionId);

    @Query("SELECT COUNT(r) FROM Response r WHERE r.question.id = :questionId AND r.isCorrect = true")
    long countCorrectResponsesByQuestionId(Long questionId);

    @Query("SELECT COUNT(r) FROM Response r WHERE r.question.id = :questionId")
    long countTotalResponsesByQuestionId(Long questionId);

    @Query("SELECT r.selectedOption.id as optionId, COUNT(r) as count FROM Response r WHERE r.question.id = :questionId GROUP BY r.selectedOption.id")
    List<Map<String, Object>> countResponsesByOptionForQuestion(Long questionId);

    @Query("SELECT r.question.id as questionId, COUNT(r) as count FROM Response r WHERE r.quizAttempt.quiz.id = :quizId AND r.isCorrect = false GROUP BY r.question.id ORDER BY COUNT(r) DESC")
    List<Map<String, Object>> findMostMissedQuestionsByQuizId(Long quizId);
}
