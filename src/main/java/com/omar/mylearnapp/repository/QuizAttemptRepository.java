package com.omar.mylearnapp.repository;

import com.omar.mylearnapp.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserId(Long userId);
    List<QuizAttempt> findByUserClerkId(String clerkId);
    List<QuizAttempt> findByQuizId(Long quizId);
    List<QuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId ORDER BY qa.startTime DESC")
    List<QuizAttempt> findRecentAttemptsByUserId(Long userId);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId")
    long countAttemptsByQuizId(Long quizId);

    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId")
    Double getAverageScoreByQuizId(Long quizId);

    @Query("SELECT AVG(qa.timeTakenSeconds) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId")
    Double getAverageTimeByQuizId(Long quizId);
    List<QuizAttempt> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

}
