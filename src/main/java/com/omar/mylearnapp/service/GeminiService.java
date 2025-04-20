package com.omar.mylearnapp.service;

import com.omar.mylearnapp.model.Quiz;
import com.omar.mylearnapp.model.Question;
import com.omar.mylearnapp.model.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model.id}")
    private String modelId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Quiz generateQuiz(String sourceType, String content, int numQuestions, String difficulty, String category) {
        try {
            String prompt = buildPrompt(sourceType, content, numQuestions, difficulty);
            String response = callGeminiAPI(prompt);
            return parseQuizResponse(response, category, difficulty);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate quiz with Gemini: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String sourceType, String content, int numQuestions, String difficulty) {
        return String.format("""
                Generate a quiz with %d multiple-choice questions about the following %s:

                %s

                The quiz should be at %s level.
                For each question, provide 4 options with exactly one correct answer.
                The first option shouldn't always be the correct one.
                Make sure the select the correct option.
                The quiz should be in french.
                Format the response as a JSON object like this:
                {
                  "title": "Quiz title",
                  "description": "Brief description",
                  "questions": [
                    {
                      "text": "Question text",
                      "options": [
                        { "text": "Option 1", "isCorrect": true },
                        { "text": "Option 2", "isCorrect": false },
                        { "text": "Option 3", "isCorrect": false },
                        { "text": "Option 4", "isCorrect": false }
                      ]
                    }
                  ]
                }
                """, numQuestions, sourceType, content, difficulty);
    }

    private String callGeminiAPI(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelId + ":generateContent?key=" + apiKey;

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }

    private Quiz parseQuizResponse(String response, String category, String difficulty) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject content = jsonResponse.getJSONArray("candidates").getJSONObject(0).getJSONObject("content");
        String text = content.getJSONArray("parts").getJSONObject(0).getString("text");

        int startIndex = text.indexOf("{");
        int endIndex = text.lastIndexOf("}") + 1;
        String jsonStr = text.substring(startIndex, endIndex);

        JSONObject quizData = new JSONObject(jsonStr);
        Quiz quiz = new Quiz();
        quiz.setTitle(quizData.getString("title"));
        quiz.setDescription(quizData.getString("description"));
        quiz.setCategory(category);
        quiz.setDifficulty(difficulty);
        quiz.setIcon("Brain");
        quiz.setColor("from-blue-500 to-cyan-600");
        quiz.setTimeLimit(300);

        List<Question> questions = new ArrayList<>();
        JSONArray questionsArray = quizData.getJSONArray("questions");

        for (int i = 0; i < questionsArray.length(); i++) {
            JSONObject qJson = questionsArray.getJSONObject(i);
            Question question = new Question();
            question.setText(qJson.getString("text"));

            List<Option> options = new ArrayList<>();
            JSONArray optionsArray = qJson.getJSONArray("options");
            for (int j = 0; j < optionsArray.length(); j++) {
                JSONObject optJson = optionsArray.getJSONObject(j);
                Option option = new Option();
                option.setText(optJson.getString("text"));
                option.setIsCorrect(optJson.getBoolean("isCorrect"));
                options.add(option);
            }

            question.setOptions(options);
            questions.add(question);
        }

        quiz.setQuestions(questions);
        return quiz;
    }
}
