package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<Map<String, Object>> getQuizStatistics(@PathVariable Long quizId) {
        try {
            Map<String, Object> statistics = statisticsService.getQuizStatistics(quizId);
            return ResponseEntity.ok(statistics);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<Map<String, Object>> getQuestionStatistics(@PathVariable Long questionId) {
        try {
            Map<String, Object> statistics = statisticsService.getQuestionStatistics(questionId);
            return ResponseEntity.ok(statistics);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        Map<String, Object> statistics = statisticsService.getSystemStatistics();
        return ResponseEntity.ok(statistics);
    }
}