package com.omar.mylearnapp.repository;

import com.omar.mylearnapp.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByTopicId(Long topicId);
    List<Quiz> findByDifficulty(String difficulty);
    List<Quiz> findByCategory(String category);
    List<Quiz> findByTopicName(String topicName);
    List<Quiz> findByProfessorId(Long professorId);
    List<Quiz> findByTopic_Name(String name);


    @Query("SELECT COUNT(q) FROM Quiz q")
    long countQuizzes();
}
