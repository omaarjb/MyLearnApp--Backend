package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.Topic;
import com.omar.mylearnapp.repository.TopicRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }

    public Optional<Topic> getTopicByName(String name) {
        return topicRepository.findByName(name);
    }

    public List<Topic> searchTopic(String searchTerm){
        return topicRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    @Transactional
    public Topic createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    @Transactional
    public Topic updateTopic(Long id, Topic topicDetails) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + id));
        topic.setName(topic.getName());
        topic.setDescription(topic.getDescription());

        return topicRepository.save(topic);

    }

    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + id));
        topicRepository.delete(topic);
    }

    public boolean existsById(Long id) {
        return topicRepository.existsById(id);
    }
    
    public boolean existsByName(String name) {
        return topicRepository.findByName(name).isPresent();
    }

    @Transactional
    public List<Topic> createMultipleTopics(List<Topic> topics) {
        return topicRepository.saveAll(topics);
    }
}
