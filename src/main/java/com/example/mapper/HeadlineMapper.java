package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pojo.dto.HeadlineHistoryDTO;
import com.example.pojo.dto.PortalPageQueryDTO;
import com.example.pojo.entity.Headline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 胡金勇
 * @description 针对表【news_headline】的数据库操作Mapper
 * @createDate 2024-03-20 20:45:22
 * @Entity com.example.pojo.entity.Headline
 */
@Mapper
public interface HeadlineMapper extends BaseMapper<Headline> {

    /**
     * 分页查询首页头条信息
     */
    IPage<Map<String, Object>> findMyPage(IPage<Map<String, Object>> iPage, @Param("portalPageQueryDTO") PortalPageQueryDTO portalPageQueryDTO);

    /**
     * 查询头条详情
     * 添加@MapKey("hid")后，map的键无法读出
     */
    Map<String, Object> findMyHeadlineDetail(Integer hid);

    /**
     * 查询历史记录(分页)
     *
     * @param iPage
     * @param headlineHistoryDTO
     */
    IPage<Map<String, Object>> findHistoryPage(IPage<Map<String, Object>> iPage, @Param("headlineHistoryDTO") HeadlineHistoryDTO headlineHistoryDTO);

    /**
     * 查询历史记录(所有,不分页)
     *
     * @param headlineHistoryDTO
     */
    List<Map<String, Object>> findHistory(@Param("headlineHistoryDTO") HeadlineHistoryDTO headlineHistoryDTO);
}




