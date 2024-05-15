package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.HeadlineMapper;
import com.example.mapper.HistoryMapper;
import com.example.pojo.dto.HeadlineDTO;
import com.example.pojo.dto.HeadlineHistoryDTO;
import com.example.pojo.dto.HeadlineUpdateDTO;
import com.example.pojo.dto.PortalPageQueryDTO;
import com.example.pojo.entity.Headline;
import com.example.pojo.entity.History;
import com.example.pojo.vo.PortalPageQueryVO;
import com.example.service.HeadlineService;
import com.example.utils.JwtHelper;
import com.example.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HeadlineServiceImpl extends ServiceImpl<HeadlineMapper, Headline> implements HeadlineService {

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private JwtHelper jwtHelper;

    /**
     * 分页查询首页头条信息
     * 1.进行分页查询
     * 2.将查询结果拼到result中
     * 注意1：查询需要自定义语句 自定义mapper的方法，携带分页
     * 注意2：返回的结果List<Map<String, Object>>
     */
    @Override
    public Result findNewsPage(PortalPageQueryDTO portalPageQueryDTO) {

        // Mybatis提供的分页查询操作 Page -> (当前页数, 页容量)
        // 没有对应的实体类来接收参数,所以IPage的接收数据类型是一个Map<String, Object>
        // IPage<Map<String, Object>> iPage = new Page<>(portalPageQueryDTO.getPageNum(), portalPageQueryDTO.getPageSize());
        //headlineMapper.findMyPage(iPage, portalPageQueryDTO);

        // 获取pageData当前页数据
        // List<Map<String, Object>> pageDataRecords = iPage.getRecords();

        // 包装pageInfo数据
        // Map<String, Object> pageInfomap = new HashMap<>();
        // pageInfomap.put("pageData", pageDataRecords);
        // pageInfomap.put("pageNum", iPage.getCurrent()); // 当前页码数
        // pageInfomap.put("pageSize", iPage.getSize());   // 当前页大小
        // pageInfomap.put("totalPage", iPage.getPages()); // 总页数
        // pageInfomap.put("totalSize", iPage.getTotal()); // 总记录数

        // QueryWrapper + Mybatis-plus自己的分页方法Page + selectPage实现分页查询
        QueryWrapper<Headline> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        if (portalPageQueryDTO.getKeyWords() != null && !portalPageQueryDTO.getKeyWords().equals("")) {
            queryWrapper.like("title", portalPageQueryDTO.getKeyWords());
        }
        if (portalPageQueryDTO.getType() != 0) {
            queryWrapper.eq("type", portalPageQueryDTO.getType());
        }
        queryWrapper.select("hid", "title", "type", "page_views", "update_time", "publisher");
        queryWrapper.orderByDesc("update_time");

        // 1.selectMaps方法(推荐),无法分页，只能自己对map集合处理
        List<Map<String, Object>> maps = headlineMapper.selectMaps(queryWrapper);
        // 数据格式:{hid=1, page_views=8, create_time=2023-05-25T09:26:20, publisher=1, title=特色产业激发乡村振兴新活力, type=1}

        // 2.selectPage()方法可能会导致空指针异常,Mybatis-plus自己的分页方法Page
        Page<Headline> page = new Page<>(portalPageQueryDTO.getPageNum(), portalPageQueryDTO.getPageSize());
        // 单表分页查询,查询指定的列
        headlineMapper.selectPage(page, queryWrapper);
        // page中的数据格式:为查询的列在Headline对象中可能会被设置为null,可能会导致空指针异常
        // (hid=1, title=特色产业激发乡村振兴新活力, article=null, type=1, publisher=1, pageViews=8, createTime=Thu May 25 09:26:20 CST 2023, updateTime=null, version=null, isDeleted=null)

        // 转换函数，用于将Headline对象转换为PortalPageQueryVO对象
        Function<Headline, PortalPageQueryVO> entityToVOConverter = headline -> {
            // 创建VO对象并复制属性
            PortalPageQueryVO portalPageQueryVO = new PortalPageQueryVO();
            // 复制属性
            portalPageQueryVO.setHid(headline.getHid());
            portalPageQueryVO.setTitle(headline.getTitle());
            portalPageQueryVO.setType(headline.getType());
            portalPageQueryVO.setPageViews(headline.getPageViews());
            portalPageQueryVO.setPastHours((int) TimeUnit.HOURS.convert(new Date().getTime() - headline.getUpdateTime().getTime(), TimeUnit.MILLISECONDS));
            portalPageQueryVO.setPublisher(headline.getPublisher());
            return portalPageQueryVO;
        };
        // 将Page<Headline>中的记录集合转换为List<PortalPageQueryVO>
        List<PortalPageQueryVO> portalPageQueryVOList = page.getRecords().stream()
                .map(entityToVOConverter) // 使用转换函数
                .collect(Collectors.toList());

        // 包装pageInfo数据
        Map<String, Object> pageInfomap = new HashMap<>();
        pageInfomap.put("pageData", portalPageQueryVOList);
        pageInfomap.put("pageNum", page.getCurrent()); // 当前页码数
        pageInfomap.put("pageSize", page.getSize());   // 当前页大小
        pageInfomap.put("totalPage", page.getPages()); // 总页数
        pageInfomap.put("totalSize", page.getTotal()); // 总记录数

        // 包装data数据
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("pageInfo", pageInfomap);

        return Result.success(datamap);
    }

    /**
     * 查询头条详情
     * 1.查询对应的数据返回给前端 【多表,头条表和用户表,方法需要自定义 返回map即可】
     * 2.修改阅读量 + 1 【只要修改表的内容，就要使用version乐观锁】
     */
    @Override
    public Result showHeadlineDetail(Integer hid, Integer uid) {
        // 多表,自定义查询方法;获取查询得到的一个map数据
        Map<String, Object> headlineDetailMapData = headlineMapper.findMyHeadlineDetail(hid);

        // 添加浏览记录
        History history = new History();

        history.setHid(hid);
        history.setUid(uid);
        history.setBrowsingTime(new Date());
        historyMapper.insert(history);

        // 包装data数据
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("headline", headlineDetailMapData);

        Headline headline = new Headline();
        headline.setHid((Integer) headlineDetailMapData.get("hid"));
        // 修改阅读量 + 1
        headline.setPageViews((Integer) headlineDetailMapData.get("pageViews") + 1);

        // version乐观锁，获取当前数据对应的版本,然后设置version
        headline.setVersion((Integer) headlineDetailMapData.get("version"));
        // 仅支持updateById(id)与update(entity, wrapper)方法
        headlineMapper.updateById(headline);

        return Result.success(datamap);
    }

    /**
     * 头条发布实现
     * 1.补全一个headline对象的全部属性(id会自增长)
     */
    @Override
    public Result publish(HeadlineDTO headlineDTO, String token) {
        // 获取publisher的userId
        int userId = jwtHelper.getUserId(token).intValue();

        // 属性拷贝
        Headline headline = new Headline();
        BeanUtils.copyProperties(headlineDTO, headline);

        headline.setPublisher(userId);
        headline.setPageViews(0);
        headline.setCreateTime(new Date());
        headline.setUpdateTime(new Date());

        // version和is_deleted会自己添加
        headlineMapper.insert(headline);

        return Result.success(null);
    }

    /**
     * 头条修改回显【点击修改显示数据】
     */
    @Override
    public Result findHeadlineByHid(Integer hid) {
        LambdaQueryWrapper<Headline> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Headline::getHid, Headline::getTitle, Headline::getArticle, Headline::getType);
        lambdaQueryWrapper.eq(Headline::getHid, hid);


        List<Map<String, Object>> headlinemaplist = headlineMapper.selectMaps(lambdaQueryWrapper);
        // 若直接返回List<Map<String, Object>> headlinemaplist,不符合响应数据格式
        // 获取List集合中第一个map集合数据
        Map<String, Object> headlinemap = headlinemaplist.get(0);

        // 包装data数据
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("headline", headlinemap);

        return Result.success(datamap);
    }

    /**
     * 头条修改实现
     * 1.设定修改的内容
     * 2.修改数据的修改时间为当前时间
     * 3.获取当前最新版本的version并进行修改
     * 4.执行updateById(headline);
     */
    @Override
    public Result updateHeadlineByHid(HeadlineUpdateDTO headlineUpdateDTO) {

        // 属性拷贝
        Headline headline = new Headline();
        BeanUtils.copyProperties(headlineUpdateDTO, headline);

        // 乐观锁
        headline.setVersion(headlineMapper.selectById(headline.getHid()).getVersion());
        headline.setUpdateTime(new Date());
        // 传入实体即可,更新时会自动匹配字段
        headlineMapper.updateById(headline);

        return Result.success(null);
    }

    /**
     * 头条删除
     * 注意是逻辑删除
     */
    @Override
    public Result removeByHid(Integer hid) {
        headlineMapper.deleteById(hid);
        return Result.success(null);
    }

    /**
     * 头条历史记录
     */
    @Override
    public Result findHistoryPage(HeadlineHistoryDTO headlineHistoryDTO) {

        // 查询所有记录
        List<Map<String, Object>> pageDataRecords = headlineMapper.findHistory(headlineHistoryDTO);

        // 使用流API来过滤和收集数据
        Map<Integer, Map<String, Object>> maxBrowsingTimeRecords = pageDataRecords.stream()
                .collect(Collectors.toMap(
                        map -> (Integer) map.get("hid"), // 使用 "hid" 作为键
                        map -> map, // 使用整个Map作为值
                        (existingValue, newValue) -> {
                            // 比较browsingTime并保留最大的
                            LocalDateTime existingTime = (LocalDateTime) existingValue.get("browsingTime");
                            LocalDateTime newTime = (LocalDateTime) newValue.get("browsingTime");
                            // 使用 isAfter 方法比较时间，返回时间更晚的记录
                            return existingTime.isAfter(newTime) ? existingValue : newValue;
                        },
                        () -> new HashMap<>()) // 提供一个Supplier用于Map的初始化，确保线程安全
                );

        // 将过滤后的数据转换回 List,uniqueRecordsList包含了按照 "hid" 去重后的记录，每个 "hid" 对应的browsing_time是最大的
        List<Map<String, Object>> uniqueRecordsList = new ArrayList<>(maxBrowsingTimeRecords.values());

        // 使用流对列表进行排序，基于browsingTime降序排列,sortedRecordsList现在包含了按照 "hid" 去重且根据 "browsingTime" 降序排列的记录
        List<Map<String, Object>> sortedRecordsList = uniqueRecordsList.stream()
                .sorted(Comparator.comparing(record -> (LocalDateTime) record.get("browsingTime"), Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // 包装pageInfo数据
        Map<String, Object> pageInfomap = new HashMap<>();
        pageInfomap.put("pageData", sortedRecordsList);

        // 包装data数据
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("pageInfo", pageInfomap);

        return Result.success(datamap);
    }
}




