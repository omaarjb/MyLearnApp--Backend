package com.omar.mylearnapp.repository;

import com.omar.mylearnapp.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic,Long> {
    Optional<Topic> findByName(String name);
    List<Topic> findByNameContainingIgnoreCase(String name);
}
