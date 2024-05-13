package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenAiChatConfig {


    // 读取配置文件中系统默认配置
    @Value("${spring.ai.openai.chat.api-key}")
    private String apiKey;
    @Value("${spring.ai.openai.chat.base-url}")
    private String baseUrl;

    /**
     * 通过配置自定义的apiKey和baseUrl访问中转/反向代理，实现面向用户的OpenAiChat客户端
     * 可通过OpenAiApi类的构造方法配置不同的基本参数
     *
     * @return 自定义的OpenAiChat客户端
     */
    @Bean("myOpenAiChatClient")
    public OpenAiChatClient myOpenAiChatClient() {
        log.info("自定义的apiKey和baseUrl访问中转/反向代理...");
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);
        return new OpenAiChatClient(openAiApi);
    }
}