package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ai")
@Slf4j
public class HeadLineAIController {

    private final OpenAiChatClient chatClient;

    /**
     * 用于初始化和注入 OpenAiChatClient 实例
     *
     * @param chatClient 与 OpenAI 服务器进行通信的客户端实例
     */
    @Autowired
    public HeadLineAIController(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }


}
