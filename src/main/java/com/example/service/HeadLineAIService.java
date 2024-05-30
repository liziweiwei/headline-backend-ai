package com.example.service;

import com.example.pojo.dto.HeadlineAiDTO;
import com.example.utils.Result;

public interface HeadLineAIService {
    /**
     * 获取新闻摘要
     *
     * @param headlineAiDTO
     * @return
     */
    Result summarize(HeadlineAiDTO headlineAiDTO);

    /**
     * 润色文章
     *
     * @param headlineAiDTO
     * @return
     */
    Result polish(HeadlineAiDTO headlineAiDTO);

}
