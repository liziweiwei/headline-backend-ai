package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.constant.MessageConstant;
import com.example.exception.AIConnectionException;
import com.example.mapper.HeadlineMapper;
import com.example.mapper.HistoryMapper;
import com.example.pojo.entity.Headline;
import com.example.pojo.entity.History;
import com.example.service.HeadLineAIService;
import com.example.utils.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HeadLineAIServiceImpl implements HeadLineAIService {

    @Resource(name = "myOpenAiChatClient")
    private OpenAiChatClient chatClient;

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private HistoryMapper historyMapper;

    /**
     * 对新闻进行总结概述
     *
     * @param hid 新闻的唯一标识符
     * @return Result对象, 包含总结后的新闻内容
     */
    @Override
    public Result summarize(Integer hid) {
        // 根据hid获取新闻文章
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
     * @param hid 新闻的唯一标识符
     * @return Result对象, 包含润色后的新闻内容
     */
    @Override
    public Result polish(Integer hid) {
        // 根据hid获取新闻文章
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
     * 分析用户的浏览历史(为类别添加推荐指数)
     *
     * @return Result对象, 包含分析结果
     */
    @Override
    public Result analyzeHistory() {

        // 查询所有浏览历史文章的标题和浏览时间
        LambdaQueryWrapper<History> lambdaTitleQueryWrapper = new LambdaQueryWrapper<>();
        lambdaTitleQueryWrapper.select(History::getTitle);
        List<Map<String, Object>> newsHistoryTitleList = historyMapper.selectMaps(lambdaTitleQueryWrapper);

        // 创建一个新的List<String>集合用于存储标题
        // 使用流API将newsHistoryTitleList中的每个Map的特定字段（例如"title"）收集到titleList中
        List<String> titleList = newsHistoryTitleList.stream()
                .map(map -> {
                    // 从Map中取出"title"字段，并确保它是String类型
                    Object titleObject = map.get("title");
                    // 检查是否为null，并且进行类型转换
                    return (String) (titleObject != null ? titleObject : "");
                })
                // 过滤掉空字符串，以确保我们不收集null或空值
                .filter(title -> !title.isEmpty())
                .collect(Collectors.toList());

        LambdaQueryWrapper<History> lambdaTimeQueryWrapper = new LambdaQueryWrapper<>();
        lambdaTimeQueryWrapper.select(History::getBrowsingTime);
        List<Map<String, Object>> newsHistoryTimeList = historyMapper.selectMaps(lambdaTimeQueryWrapper);

        // 创建一个新的List<LocalDateTime>集合用于存储时间
        // 使用流API将newsHistoryTimeList中的每个Map的特定字段（例如"browsing_time"）收集到timeList中
        List<LocalDateTime> timeList = newsHistoryTimeList.stream()
                .map(map -> {
                    Object timeObject = map.get("browsing_time");
                    assert (timeObject != null ? timeObject : "") instanceof LocalDateTime;
                    return (LocalDateTime) timeObject;
                })
                .filter(time -> !time.toString().isEmpty())
                .collect(Collectors.toList());

        // 创建用于分析的prompt
        final String promptText = """
                读取{titleList}中的所有的新闻标题
                读取{timeList}新闻的浏览时间，与上述新闻标题一一对应
                哪一类新闻标题的数量多，表示用户喜欢程度高一些，
                哪一些类别的新闻的浏览时间比较新，表示用户喜欢程度高一些，
                分析用户对各类新闻的喜好程度，
                为下面五个类别{item1}、{item2}、{item3}、{item4}、{item5}分别生成一个推荐系数，
                尽量保证每个类别的推荐系数不一样，
                输出的结果形式只能是:"{item1}: 0.2"，不要分析过程
                """;
        final PromptTemplate promptTemplate = new PromptTemplate(promptText);
        promptTemplate.add("titleList", titleList);
        promptTemplate.add("timeList", timeList);
        promptTemplate.add("item1", "新闻");
        promptTemplate.add("item2", "体育");
        promptTemplate.add("item3", "娱乐");
        promptTemplate.add("item4", "科技");
        promptTemplate.add("item5", "其他");

        // 调用OpenAI进行分析
        String result = "";
        if (chatClient != null) {
            ChatResponse chatResponse = chatClient.call(promptTemplate.create());
            if (chatResponse != null) {
                Generation generation = chatResponse.getResult();
                if (generation != null) {
                    result = generation.getOutput().getContent();
                }
            }
        }
        if (result.equals("")) {
            throw new AIConnectionException(MessageConstant.AI_Connection_FAILURE);
        }
        log.info("result = " + result);

        // 收集各个类别的推荐指数
        Map<Integer, Double> categoryMap = new HashMap<>();
        // 按行分割字符串
        String[] lines = result.split("\n");
        // 枚举类别的枚举类型
        enum Category {
            新闻(1), 体育(2), 娱乐(3), 科技(4), 其他(5);

            private final int type;

            Category(int type) {
                this.type = type;
            }

            public int getType() {
                return type;
            }
        }

        // 遍历每一行数据
        for (String line : lines) {
            // 分离键和值
            String[] parts = line.trim().split(": ");
            String categoryStr = parts[0].trim();
            double weight = Double.parseDouble(parts[1]);

            // 根据类别字符串找到枚举类型，然后获取其type
            for (Category category : Category.values()) {
                if (categoryStr.equals(category.name())) {
                    categoryMap.put(category.getType(), weight);
                    break;
                }
            }
        }

        // 遍历Map;存入或者更新数据库(news_recommendation)新闻类别推荐指数表

        return null;
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
