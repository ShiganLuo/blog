package com.baofeng.blog.service.impl;

import com.baofeng.blog.service.DashBoardService;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminDashBoradDTO.ArticleAddInThisYearResponse;
import com.baofeng.blog.dto.admin.AdminDashBoradDTO.BlogDetailNumberResponse;
import com.baofeng.blog.dto.admin.AdminDashBoradDTO.DictTemplateResponse;
import com.baofeng.blog.dto.admin.AdminDashBoradDTO.UserAddInLastWeekResponse;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.CategoryMapper;
import com.baofeng.blog.mapper.TagMapper;
import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.mapper.ImageMapper;
import com.baofeng.blog.mapper.LikeMapper;
import com.baofeng.blog.mapper.BlogSettingMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class DashBoardServiceImpl implements DashBoardService{

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ImageMapper imageMapper;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final BlogSettingMapper blogSettingMapper;
    private static final Logger logger = LoggerFactory.getLogger(DashBoardService.class);

    public DashBoardServiceImpl(UserMapper userMapper,
                                ArticleMapper articleMapper,
                                CategoryMapper categoryMapper,
                                TagMapper tagMapper,
                                ImageMapper imageMapper,
                                LikeMapper likeMapper,
                                CommentMapper commentMapper,
                                BlogSettingMapper blogSettingMapper) {
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.imageMapper = imageMapper;
        this.likeMapper = likeMapper;
        this.commentMapper = commentMapper;
        this.blogSettingMapper = blogSettingMapper;
    }

    @Override
    public ApiResponse<List<BlogDetailNumberResponse>> getBlogDetailNumber() {
        // 1. 定义时间点
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime oneWeekAgo = currentTime.minusWeeks(1);

        // 2. 定义静态配置列表（解耦数据和逻辑）
        List<StatConfig> configs = createStatConfigs();
        
        // 3. 使用 Stream API 处理列表，调用公共方法
        List<BlogDetailNumberResponse> resultList = configs.stream()
            .map(config -> createStatResponse(config, oneWeekAgo, currentTime))
            .collect(Collectors.toList());

        return ApiResponse.success(resultList);
    }

    // ----------------------------------------------------------------------------------

    /**
     * 封装统计项的初始配置
     */
    private static class StatConfig {
        final String des;
        final String icon;
        // 假设 Mapper 都是接口，使用函数式接口 MapperFunction
        final BiFunction<LocalDateTime, LocalDateTime, Long[]> dataFetcher; 

        // 构造函数
        StatConfig(String des, String icon, BiFunction<LocalDateTime, LocalDateTime, Long[]> dataFetcher) {
            this.des = des;
            this.icon = icon;
            this.dataFetcher = dataFetcher;
        }
    }

    /**
     * 创建所有统计项的配置
     */
    private List<StatConfig> createStatConfigs() {
        return List.of(
            new StatConfig("文章数", "&#xe7ae", (oneWeekAgo, currentTime) -> new Long[] {
                articleMapper.selectArticleCountWhenSpecifiedTime(oneWeekAgo),
                articleMapper.selectArticleCountWhenSpecifiedTime(currentTime)
            }),
            new StatConfig("分类数", "&#xe723", (oneWeekAgo, currentTime) -> new Long[] {
                categoryMapper.selectCategoryCountWhenSpecifiedTime(oneWeekAgo),
                categoryMapper.selectCategoryCountWhenSpecifiedTime(currentTime)
            }),
            new StatConfig("标签数", "&#xe70b", (oneWeekAgo, currentTime) -> new Long[] {
                tagMapper.selectTagCountWhenSpecifiedTime(oneWeekAgo),
                tagMapper.selectTagCountWhenSpecifiedTime(currentTime)
            }),
            new StatConfig("评论数", "&#xe6e9", (oneWeekAgo, currentTime) -> new Long[] {
                commentMapper.selectCommentCountWhenSpecifiedTime(oneWeekAgo),
                commentMapper.selectCommentCountWhenSpecifiedTime(currentTime)
            }),
            new StatConfig("图片数", "&#xe6ee", (oneWeekAgo, currentTime) -> new Long[] {
                imageMapper.selectImageCountWhenSpecifiedTime(oneWeekAgo),
                imageMapper.selectImageCountWhenSpecifiedTime(currentTime)
            }),
            new StatConfig("点赞数", "&#xe6eb", (oneWeekAgo, currentTime) -> new Long[] {
                likeMapper.selectLikeCountWhenSpecifiedTime(oneWeekAgo),
                likeMapper.selectLikeCountWhenSpecifiedTime(currentTime)
            }),
            new StatConfig("用户数", "&#xe608", (oneWeekAgo, currentTime) -> new Long[] {
                userMapper.selectUserCountWhenSpecifiedTime(oneWeekAgo),
                userMapper.selectUserCountWhenSpecifiedTime(currentTime)
            })
        );
    }

    /**
     * 公共核心逻辑：查询数据，计算百分比，创建响应对象
     */
    private BlogDetailNumberResponse createStatResponse(StatConfig config, LocalDateTime oneWeekAgo, LocalDateTime currentTime) {
        // 1. 获取数据
        Long[] counts = config.dataFetcher.apply(oneWeekAgo, currentTime);
        long startVal = counts[0];
        long endVal = counts[1];
        
        // 2. 计算百分比
        String change = calculatePercentage(endVal, startVal);

        // 3. 构建响应对象 (假设 BlogDetailNumberResponse 有 Builder 或 AllArgs 构造函数)
        return BlogDetailNumberResponse.builder()
            .des(config.des)
            .icon(config.icon)
            .duration(1000L) // 固定值
            .startVal(startVal)
            .endVal(endVal)
            .change(change)
            .build();
    }

    /**
     * 提取的百分比计算方法，处理除以零和精度问题
     */
    private String calculatePercentage(long endVal, long startVal) {
        if (startVal == 0) {
            // 如果分母为0，且有新增，则变化率是无限大，返回 "+∞" 或 "+100%"
            return (endVal > 0) ? "+100.00%" : "+0.00%"; 
        }
        
        // 注意：计算时使用 double 类型，避免整数除法丢失精度
        double rate = (double) (endVal - startVal) / startVal;
        double percentage = rate * 100;
        
        // 使用 String.format() 格式化，%+ 保证正数有 + 号
        return String.format("%+.2f%%", percentage);
    }
    
    @Override
    public ApiResponse<UserAddInLastWeekResponse> getUserAddInLastWeek() {
        try {
            LocalDate today = LocalDate.now();
            // 查询累计用户数
            List<Long> cumulative = IntStream.rangeClosed(0, 7)
                .mapToObj(i -> {
                    LocalDate date = today.minusDays(7 - i);
                    LocalDateTime endTime = date.atTime(LocalTime.of(23, 59, 59));
                    Long total = userMapper.selectUserCountWhenSpecifiedTime(endTime);
                    Long safeTotal = Optional.ofNullable(total).orElse(0L);
                    return safeTotal;
                })
                .collect(Collectors.toList());
            
            // 计算每日新增数（防止负数）
            List<Long> counts = IntStream.range(1, cumulative.size())
                .mapToObj(i -> Math.max(cumulative.get(i) - cumulative.get(i - 1), 0L))
                .collect(Collectors.toList());

            // 日期列表（默认字符串格式 yyyy-MM-dd）
            List<String> days = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> today.minusDays(7 - i).toString())
                .collect(Collectors.toList());
            UserAddInLastWeekResponse userAddInLastWeekResponse = new UserAddInLastWeekResponse();
            userAddInLastWeekResponse.setCounts(counts != null ? counts : List.of());
            userAddInLastWeekResponse.setDays(days != null ? days : List.of());

            return ApiResponse.success(userAddInLastWeekResponse);
        } catch (Exception e) {
            logger.error("获取最近7天新增用户数失败:", e.getMessage());
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"获取最近7天新增用户数失败");
        }
        
    }

    @Override
    public ApiResponse<String> getUserAddComparedToLastWeek() {
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime oneWeekAgo = currentTime.minusWeeks(1);
        Long startVal = userMapper.selectUserCountWhenSpecifiedTime(oneWeekAgo);
        Long endVal = userMapper.selectUserCountWhenSpecifiedTime(currentTime);
        String change = calculatePercentage(endVal, startVal);
        return ApiResponse.success(change);
    }

    @Override
    public ApiResponse<List<DictTemplateResponse>> getAccessAndUserCondition() {
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime oneWeekAgo = currentTime.minusWeeks(1);
        final LocalDateTime oneMonthAgo = currentTime.minusMonths(1);
        final LocalDateTime oneYearAgo = currentTime.minusYears(1);
        Long endVal = userMapper.selectUserCountWhenSpecifiedTime(currentTime);
        Long weekStart = userMapper.selectUserCountWhenSpecifiedTime(oneWeekAgo);
        Long monthStart = userMapper.selectUserCountWhenSpecifiedTime(oneMonthAgo);
        Long yearStart = userMapper.selectUserCountWhenSpecifiedTime(oneYearAgo);
        String weekChange = calculatePercentage(endVal, weekStart);
        String monthChange = calculatePercentage(endVal, monthStart);
        String yearChange = calculatePercentage(endVal, yearStart);
        Long visitCount = blogSettingMapper.selectvisitCountById((long) 1); //默认第一个网站设置为本博客
        List<DictTemplateResponse> dicts = new ArrayList<>();
        dicts.add(new DictTemplateResponse("周增长", weekChange));
        dicts.add(new DictTemplateResponse("月增长", monthChange));
        dicts.add(new DictTemplateResponse("年增长", yearChange));
        dicts.add(new DictTemplateResponse("访问量", String.valueOf(visitCount)));
        return ApiResponse.success(dicts);
    }

    @Override
    public ApiResponse<ArticleAddInThisYearResponse> getArticleAddInThisYear() {
        final LocalTime endTime = LocalTime.of(23, 59, 59);
        final int currentYear = LocalDate.now().getYear();
        final int previousYear = currentYear - 1;
        // 去年12月31日23:59:59
        
        LocalDateTime endOfLastYear = Year.of(previousYear)
            .atMonth(Month.DECEMBER)
            .atDay(1)
            .with(TemporalAdjusters.lastDayOfMonth())
            .atTime(endTime);
                                          
        Stream<LocalDateTime> previousYearStream = Stream.of(endOfLastYear);
        Stream<LocalDateTime> currentYearMonthsStream = Arrays.stream(Month.values())
            .map(month -> 
                Year.of(currentYear)
                    .atMonth(month)
                    .atDay(1)
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .atTime(endTime)
            );
        List<LocalDateTime> endDates = Stream.concat(previousYearStream, currentYearMonthsStream)
            .collect(Collectors.toList());
        // 累计文章数
        List<Long> cumulative = endDates.stream()
            .map(endDateTime -> {
                Long articleCount = articleMapper.selectArticleCountWhenSpecifiedTime(endDateTime);
                Long safeArticleCount = Optional.ofNullable(articleCount).orElse(0L);
                return safeArticleCount;
            })
            .collect(Collectors.toList());
        
        // 每月文章新增数
        List<Long> articleCounts = IntStream.range(1, cumulative.size())
            .mapToObj(i -> Math.max(cumulative.get(i) - cumulative.get(i - 1), 0L))
            .collect(Collectors.toList());
        // 月份列表
        List<String> months = Arrays.stream(Month.values())
            .map(month -> {
                int monthValue = month.getValue();
                return monthValue + "月";
            })
            .collect(Collectors.toList());
        ArticleAddInThisYearResponse articleAddInThisYearResponse = new ArticleAddInThisYearResponse();
        articleAddInThisYearResponse.setCounts(articleCounts);
        articleAddInThisYearResponse.setMonths(months);
        return ApiResponse.success(articleAddInThisYearResponse);
    }

    @Override
    public ApiResponse<String> getArticleAddCompareToLastWeek() {
        final LocalDateTime currentTime = LocalDateTime.now();
        final LocalDateTime oneWeekAgo = currentTime.minusWeeks(1);
        Long startVal = articleMapper.selectArticleCountWhenSpecifiedTime(oneWeekAgo);
        Long endVal = articleMapper.selectArticleCountWhenSpecifiedTime(currentTime);
        String change = calculatePercentage(endVal, startVal);
        return ApiResponse.success(change);
    }
}
