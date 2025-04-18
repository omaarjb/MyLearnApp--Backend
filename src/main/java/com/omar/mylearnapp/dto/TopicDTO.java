package com.omar.mylearnapp.dto;

import com.omar.mylearnapp.model.Topic;

public class TopicDTO {
    private Long id;
    private String name;
    private String description;

    public TopicDTO(){}

    public TopicDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static TopicDTO fromTopic(Topic topic){
        TopicDTO dto = new TopicDTO();
        dto.setId(topic.getId());
        dto.setName(topic.getName());
        dto.setDescription(topic.getDescription());

        return dto;
    }
}
