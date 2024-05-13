package com.example.controller;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class OpenAIChatController {

    private final OpenAiChatClient chatClient;

    @Autowired
    public OpenAIChatController(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 生成对应的AI回复
     *
     * @param message 用户输入的消息，默认为“介绍自己”。
     * @return 返回一个包含生成内容的Map对象，其中key为"generation"，value为AI生成的回复内容
     */
    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "介绍自己") String message) {
        // 调用ChatClient的call方法，传入用户消息，获取AI生成的回复
        return Map.of("generation", chatClient.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt);
    }

    // 流,类似文件下载
    @GetMapping(value = "/ai/generateStreamTxt", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStreamTxt(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Flux<String> stream = chatClient.stream(message);
        return stream;
    }
}
