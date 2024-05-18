package com.example.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.mapper.HistoryMapper;
import com.example.mapper.RecommendationMapper;
import com.example.pojo.entity.History;
import com.example.pojo.entity.Recommendation;
import com.example.properties.UserIdProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HeadlineHistoryTask {

    @Resource(name = "myOpenAiChatClient")
    private OpenAiChatClient chatClient;

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private RecommendationMapper recommendationMapper;

    @Autowired
    private UserIdProperties userIdProperties;


    /**
     * 分析用户的浏览历史(为类别添加推荐指数),自动更新推荐系数
     */
    @Scheduled(cron = "0 0/10 * * * ?") // 每10分钟执行一次
    public void updateRecommendCoefficient() {

        // 获取当前登录用户的id
        Integer userId = userIdProperties.getUserId().intValue();
        log.info("开始分析 用户id:{} 的浏览历史,自动更新该用户各类新闻的推荐系数...", userId);

        // 创建一个Map<Integer, Double>用于存储每个类别的推荐系数
        Map<Integer, Double> categoryMap = new HashMap<>();

        LambdaQueryWrapper<History> queryHistoryWrapperByUserId = new LambdaQueryWrapper<>();
        queryHistoryWrapperByUserId.eq(History::getUid, userId);
        List<History> historyList = historyMapper.selectList(queryHistoryWrapperByUserId);

        // 如果当前用户有浏览历史
        if (!historyList.isEmpty()) {
            // 查询当前用户的所有浏览历史文章的标题
            LambdaQueryWrapper<History> lambdaTitleQueryWrapper = new LambdaQueryWrapper<>();
            lambdaTitleQueryWrapper.eq(History::getUid, userId);
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

            // 查询当前用户的所有浏览记录的浏览时间
            LambdaQueryWrapper<History> lambdaTimeQueryWrapper = new LambdaQueryWrapper<>();
            lambdaTimeQueryWrapper.eq(History::getUid, userId);
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
                    结果形式只能为:"{item1}: 0.2"，不要分析过程
                    """;
            final PromptTemplate promptTemplate = new PromptTemplate(promptText);
            promptTemplate.add("titleList", titleList);
            promptTemplate.add("timeList", timeList);
            promptTemplate.add("item1", "新闻");
            promptTemplate.add("item2", "体育");
            promptTemplate.add("item3", "娱乐");
            promptTemplate.add("item4", "科技");
            promptTemplate.add("item5", "其他");

            // 记录调用OpenAI API获取的结果
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
            log.info("result: " + result);

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
        } else {
            // 如果该用户没有历史记录，则将该用户各个类别的推荐系数设置为0
            categoryMap.put(1, 0.0);
            categoryMap.put(2, 0.0);
            categoryMap.put(3, 0.0);
            categoryMap.put(4, 0.0);
            categoryMap.put(5, 0.0);
        }

        // 遍历Map;存入或者更新数据库(news_recommendation)新闻类别推荐指数表
        LambdaQueryWrapper<Recommendation> queryWrapperByUserId = new LambdaQueryWrapper<>();
        queryWrapperByUserId.eq(Recommendation::getUid, userId);
        List<Recommendation> recommendationList = recommendationMapper.selectList(queryWrapperByUserId);

        if (!recommendationList.isEmpty()) {
            // 遍历Map,更新数据库(news_recommendation)
            for (Map.Entry<Integer, Double> entry : categoryMap.entrySet()) {
                Integer categoryId = entry.getKey();
                Double recommendCoefficient = entry.getValue();

                // 更新推荐系数
                LambdaUpdateWrapper<Recommendation> updateWrapperRecommend = new LambdaUpdateWrapper<>();
                updateWrapperRecommend.eq(Recommendation::getUid, userId)
                        .eq(Recommendation::getType, categoryId)
                        .set(Recommendation::getRecommendCoefficient, recommendCoefficient);
                recommendationMapper.update(updateWrapperRecommend);
            }
        } else {
            log.info("用户id发生变化,变成 用户id:{},需要清空表(news_recommendation)的内容...", userId);
            // 先清空表的内容(与userId不相同的列)(与MyBatis-Plus阻止全表更新删除插件相冲突)，再插入数据
            LambdaQueryWrapper<Recommendation> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.ne(Recommendation::getUid, userId);
            recommendationMapper.delete(deleteWrapper);

            // 遍历Map,插入数据(news_recommendation)
            for (Map.Entry<Integer, Double> entry : categoryMap.entrySet()) {
                Integer categoryId = entry.getKey();
                Double recommendCoefficient = entry.getValue();

                // 插入推荐系数
                Recommendation recommendation = new Recommendation();
                recommendation.setUid(userId);
                recommendation.setType(categoryId);
                recommendation.setRecommendCoefficient(recommendCoefficient);
                recommendationMapper.insert(recommendation);
            }
        }
    }

}
