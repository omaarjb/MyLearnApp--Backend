package com.omar.mylearnapp.repository;

import com.omar.mylearnapp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizId(Long quizId);
    int countByQuizId(Long quizId);

    @Query("SELECT q FROM Question q WHERE q.quiz.id = :quizId ORDER BY FUNCTION('RAND')")
    List<Question> findRandomQuestionsByQuizId(Long quizId);

    @Query("SELECT q FROM Question q WHERE q.text LIKE %:searchText%")
    List<Question> searchByText(String searchText);
}
