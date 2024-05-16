package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.dto.HeadlineDTO;
import com.example.pojo.dto.HeadlineHistoryDTO;
import com.example.pojo.dto.HeadlineUpdateDTO;
import com.example.pojo.dto.PortalPageQueryDTO;
import com.example.pojo.entity.Headline;
import com.example.utils.Result;

/**
 * @author 胡金勇
 * @description 针对表【news_headline】的数据库操作Service
 * @createDate 2024-03-20 20:45:23
 */
public interface HeadlineService extends IService<Headline> {

    /**
     * 分页查询首页头条信息
     */
    Result findNewsPage(PortalPageQueryDTO portalPageQueryDTO);

    /**
     * 查询头条详情
     */
    Result showHeadlineDetail(Integer hid, Integer uid);

    /**
     * 头条发布实现
     */
    Result publish(HeadlineDTO headlineDTO, String token);

    /**
     * 修改头条回显
     */
    Result findHeadlineByHid(Integer hid);

    /**
     * 头条修改实现
     */
    Result updateHeadlineByHid(HeadlineUpdateDTO headlineUpdateDTO);

    /**
     * 删除头条
     */
    Result removeByHid(Integer hid);

    /**
     * 查询头条历史记录
     */
    Result findHistoryPage(HeadlineHistoryDTO headlineDTO);

    /**
     * 删除头条历史记录
     */
    Result removeHistory(Integer id);
}
