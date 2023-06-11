package com.example.project2.service;


import com.example.project2.controller.MyControllerLogger;
import com.example.project2.model.ChatGptResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatGptService {
    @Value("${openai.api.url}")
    private String url;
    @Value("${openai.api.key}")
    private String apiKey;
    private static final Logger logger = LogManager.getLogger(MyControllerLogger.class);

    public ChatGptResponse getChatResponse(String input) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Build input payload
            logger.info("\nProcessing getChatResponse...");
            logger.info("\nInput: "+input);
            JSONObject payload = new JSONObject();
            JSONObject message = new JSONObject();
            JSONArray messageList = new JSONArray();

            message.put("role", "user");
            message.put("content", input);
            messageList.put(message);

            payload.put("model", "gpt-3.5-turbo");
            payload.put("messages", messageList);
            payload.put("temperature", 0.7);

            StringEntity inputEntity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);

            // POST request
            HttpPost post = new HttpPost(url);
            post.setEntity(inputEntity);
            post.setHeader("Authorization", "Bearer " + apiKey);
            post.setHeader("Content-Type", "application/json");

            // Send POST request and parse response
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity resEntity = response.getEntity();
                String resJsonString = new String(resEntity.getContent().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject resJson = new JSONObject(resJsonString);

                if (resJson.has("error")) {
                    String errorMsg = resJson.getString("error");
                    return new ChatGptResponse(HttpStatus.BAD_REQUEST, errorMsg);
                }

                // Parse JSON response
                JSONArray responseArray = resJson.getJSONArray("choices");
                List<String> responseList = new ArrayList<>();

                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject responseObj = responseArray.getJSONObject(i);
                    String responseString = responseObj.getJSONObject("message").getString("content");
                    responseList.add(responseString);
                }

                logger.info("\nOutput: " + responseList.get(0));
                return new ChatGptResponse(HttpStatus.OK,responseList.get(0));

            } catch (IOException e) {
                logger.error("Error occurred in getChatResponse", e);
                return new ChatGptResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Error occurred in getChatResponse", e);
            return new ChatGptResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}