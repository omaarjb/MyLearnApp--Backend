package com.omar.mylearnapp.repository;

import com.omar.mylearnapp.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByQuestionId(Long questionId);
    Optional<Option> findByQuestionIdAndIsCorrect(Long questionId, boolean isCorrect);

    @Query("SELECT o FROM Option o WHERE o.question.id = :questionId AND o.isCorrect = true")
    Optional<Option> findCorrectOptionByQuestionId(Long questionId);

    @Query("SELECT COUNT(o) FROM Option o WHERE o.question.id = :questionId")
    int countByQuestionId(Long questionId);

}
