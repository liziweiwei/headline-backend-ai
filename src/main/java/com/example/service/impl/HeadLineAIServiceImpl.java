package com.example.service.impl;

import com.example.constant.MessageConstant;
import com.example.exception.AIConnectionException;
import com.example.mapper.HeadlineMapper;
import com.example.pojo.dto.HeadlineAiDTO;
import com.example.pojo.entity.Headline;
import com.example.service.HeadLineAIService;
import com.example.utils.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HeadLineAIServiceImpl implements HeadLineAIService {

    @Resource(name = "myOpenAiChatClient")
    private OpenAiChatClient chatClient;

    @Autowired
    private HeadlineMapper headlineMapper;

    /**
     * 对新闻进行总结概述
     *
     * @param headlineAiDTO
     * @return Result对象, 包含总结后的新闻内容
     */
    @Override
    public Result summarize(HeadlineAiDTO headlineAiDTO) {
        // 根据hid获取新闻文章
        Integer hid = headlineAiDTO.getHid();
        Headline headline = headlineMapper.selectById(hid);
        String article = headline.getArticle();

        // 创建用于AI总结的prompt
        Prompt prompt = getSummarizePrompt(article);

        // 调用聊天客户端进行新闻内容的总结
        String reslut = "";
        if (chatClient != null) {
            ChatResponse chatResponse = chatClient.call(prompt);
            if (chatResponse != null) {
                Generation generation = chatResponse.getResult();
                if (generation != null) {
                    reslut = generation.getOutput().getContent();
                }
            }
        }

        if (reslut.equals("")) {
            throw new AIConnectionException(MessageConstant.AI_Connection_FAILURE);
        }

        // 包装data数据
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("headlinesummary", reslut);

        return Result.success(datamap);
    }

    /**
     * 对新闻进行润色
     *
     * @param headlineAiDTO
     * @return Result对象, 包含润色后的新闻内容
     */
    @Override
    public Result polish(HeadlineAiDTO headlineAiDTO) {

        // 根据hid获取新闻文章
        Integer hid = headlineAiDTO.getHid();
        Headline headline = headlineMapper.selectById(hid);
        String article = headline.getArticle();

        // 创建用于AI润色的prompt
        Prompt prompt = getPolishPrompt(article);

        // 调用聊天客户端进行新闻内容的润色
        String reslut = "";
        if (chatClient != null) {
            ChatResponse chatResponse = chatClient.call(prompt);
            if (chatResponse != null) {
                Generation generation = chatResponse.getResult();
                if (generation != null) {
                    reslut = generation.getOutput().getContent();
                }
            }
        }

        if (reslut.equals("")) {
            throw new AIConnectionException(MessageConstant.AI_Connection_FAILURE);
        }

        // 包装data数据
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("headlinepolish", reslut);

        return Result.success(datamap);
    }


    /**
     * 根据提供的消息创建一个Prompt对象
     *
     * @param message 用户的消息内容
     * @return 返回一个包含用户消息和系统消息的Prompt对象
     */
    private Prompt getSummarizePrompt(String message) {
        // 初始化系统提示模板
        String systemPrompt = "{prompt}";
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);

        // 创建用户消息对象
        Message userMessage = new UserMessage(message);

        // 使用模板和指定的提示内容创建系统消息
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("prompt", "将这篇新闻总结成为几段话，分点论述，具有准确性、简洁性、通俗性、客观性的特点"));

        // 创建并返回一个包含用户消息和系统消息的Prompt对象
        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        return prompt;
    }

    /**
     * 根据提供的消息创建一个Prompt对象
     *
     * @param message 用户的消息内容
     * @return 返回一个包含用户消息和系统消息的Prompt对象
     */
    private Prompt getPolishPrompt(String message) {
        // 初始化系统提示模板
        String systemPrompt = "{prompt}";
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);

        // 创建用户消息对象
        Message userMessage = new UserMessage(message);

        // 使用模板和指定的提示内容创建系统消息
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("prompt", "将这篇新闻进行润色，让新闻的内容充实，同时满足真实、客观、公正、平衡的特点，只返回润色后的文章"));

        // 创建并返回一个包含用户消息和系统消息的Prompt对象
        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        return prompt;
    }
}
