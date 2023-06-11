package com.example.project2.controller;

import com.example.project2.model.ChatGptResponse;
import com.example.project2.model.InputForm;
import com.example.project2.service.ChatGptService;
import com.example.project2.service.StringToHTML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ChatController {

    @Autowired
    private ChatGptService chatGptService;

    @RequestMapping("/")
    public String Form(Model model) {
        model.addAttribute("inputForm", new InputForm());
        return "chat";
    }
    @PostMapping("/message")
    public String processForm(InputForm inputForm, Model model) {
        ChatGptResponse response = generateResponse(inputForm.getInputText());
        if(!response.getStatus().equals(HttpStatus.OK)){
            return "error";
        }
        model.addAttribute("outputText", response.getMessage());
        return "chat";
    }
    private ChatGptResponse generateResponse(String inputText) {
        ChatGptResponse response = chatGptService.getChatResponse(inputText);
        response.setMessage(StringToHTML.stringToHTMLString(response.getMessage()));
        return  response;
    }
}