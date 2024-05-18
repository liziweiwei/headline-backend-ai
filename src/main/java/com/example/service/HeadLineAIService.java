package com.example.service;

import com.example.utils.Result;

public interface HeadLineAIService {
    /**
     * 获取新闻摘要
     *
     * @param hid
     * @return
     */
    Result summarize(Integer hid);

    /**
     * 润色文章
     *
     * @param hid
     * @return
     */
    Result polish(Integer hid);

    /**
     * 分析浏览历史
     */
    Result analyzeHistory();
}
