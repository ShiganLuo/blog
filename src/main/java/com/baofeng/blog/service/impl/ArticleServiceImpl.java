package com.baofeng.blog.service.impl;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminArticleVO.*;
import com.baofeng.blog.vo.common.Article.*;
import com.baofeng.blog.vo.front.FrontArticleVO.*;
import com.baofeng.blog.util.ArticleConvert;
import com.baofeng.blog.util.ResultCode;
import com.baofeng.blog.entity.*;
import com.baofeng.blog.mapper.*;
import com.baofeng.blog.service.ArticleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;



@Service
// 替代@Autowerid显示注入
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;
    private final ImageMapper imageMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public ApiResponse<Long> createArticle(CreateArticleRequest articleRequest){
        Article article = new Article();
        article.setTitle(articleRequest.title());
        article.setContent(articleRequest.content());
        article.setSummary(articleRequest.summary());
        article.setLikes((long) 0 );
        article.setViews((long) 0 );
        Long authorId = userMapper.getIdByUsername(articleRequest.author());
        if ( authorId == null) {
            return ApiResponse.error(ResultCode.PARAM_ERROR,"用户不存在");
        }
        article.setAuthorId(authorId);
        article.setStatus(Article.ArticleStatus.DRAFT);
        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        int rowsInserted = articleMapper.insertArticle(article);

        if(rowsInserted > 0){
            //插入后自动回填文章id
            Long articleId = article.getId();
            return ApiResponse.success(articleId);
        }else{
            return ApiResponse.error(ResultCode.SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<String> deleteArticle(Long id){
        int rowsDeleted = articleMapper.deleteArticle(id);
        return rowsDeleted > 0 
            ? ApiResponse.success()
            : ApiResponse.error(ResultCode.NOT_FOUND,"文章未找到");
    }

    @Override
    public ApiResponse<Article> getArticleById(Long id){
        Article article = articleMapper.getArticleById(id);
        return article != null 
            ? ApiResponse.success(article)
            : ApiResponse.error(ResultCode.NOT_FOUND);
    }

    @Override
    public ApiResponse<String> updateArticleSelective(Article article){
        int rowsUpdated = articleMapper.updateArticleSelective(article);
        return rowsUpdated > 0 
            ? ApiResponse.success()
            : ApiResponse.error(ResultCode.PARAM_ERROR);
    }

    @Override
    public ApiResponse<String> updatePinStaus(Long id,boolean isPinned){
        Article article = new Article();
        article.setId(id);
        article.setIsFeatured(isPinned);
        int rowsUpdated = articleMapper.updateArticleSelective(article);

        return rowsUpdated > 0
            ? ApiResponse.success()
            : ApiResponse.error(ResultCode.PARAM_ERROR);
    }

    @Override
    public ApiResponse<ArticlePageResponseVO> getArticlePage(ArticlePageRequestVO request) {
        // 参数校验
        int pageNum = request.pageNum() != null ? request.pageNum() : 1;
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;
        
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<ArticleVO> list = articleMapper.getArticlePage(request);
        // 获取分页信息
        PageInfo<ArticleVO> pageInfo = new PageInfo<>(list);
        
        // 封装返回结果
        ArticlePageResponseVO response = new ArticlePageResponseVO();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setPages(pageInfo.getPages());    // 总页数
        response.setList(pageInfo.getList());      // 当前页数据
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<ArticleDetailResponse> getArticlePageFormById(Long id) {
        ArticleVO articleVO = articleMapper.getArticlePageFormById(id);
        ArticleDetailResponse articleDetailResponse = ArticleConvert.convertToDetailResponse(articleVO);
        return ApiResponse.success(articleDetailResponse);
    }
    @Override
    public ApiResponse<String> publishArticle(Long articleId,Long authorId) {
        Long articleAuthorId = articleMapper.getAuthorIdById(articleId);
        if ( articleAuthorId == authorId ) {
            Article article = new Article();
            article.setId(articleId);
            article.setStatus(Article.ArticleStatus.PUBLISHED);
            article.setPublishedAt(LocalDateTime.now());
            int rowsUpdated = articleMapper.updateArticleSelective(article);
            return rowsUpdated > 0
                ? ApiResponse.success()
                : ApiResponse.error(ResultCode.SERVER_ERROR);
        } else {
            return ApiResponse.error(ResultCode.PARAM_ERROR, "作者ID与文章实际作者ID不一致");
        }
    }
    @Override
    public ApiResponse<String> isTitleExist(String title){
        boolean isDuplicated = articleMapper.isTitleExist(title);
        return isDuplicated
            ? ApiResponse.error(ResultCode.PARAM_ERROR,"标题已存在")
            : ApiResponse.success("可以使用该标题");
    }
    /**
     * 存储图片到服务器并返回相对路径
     * @param imageFile 上传的图片文件
     * @return 图片的相对路径
     * @throws IOException 如果存储失败
     */
    @Override
    /**
     * 存储图片到服务器并返回相对路径
     * @param imageFile 上传的图片文件
     * @return 图片的相对路径
     * @throws IOException 如果存储失败
     */
    public ApiResponse<String> storeImage(MultipartFile imageFile,Long articleId) {
        Article article1 = articleMapper.getArticleById(articleId);
        if ( article1 == null ) {
            return ApiResponse.error(ResultCode.PARAM_ERROR,"文章不存在");
        }
        // 检查文件是否为空
        if (imageFile == null || imageFile.isEmpty()) {
            return ApiResponse.error(ResultCode.PARAM_ERROR, "文件不能为空");
        }

        // 获取文件名并检查
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return ApiResponse.error(ResultCode.PARAM_ERROR, "文件名不能为空");
        }

        // 检查文件扩展名
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ApiResponse.error(ResultCode.PARAM_ERROR, "文件没有扩展名");
        }

        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            e.printStackTrace(); 
            return ApiResponse.error(ResultCode.SERVER_ERROR,"创建目录失败");
        }
        
        // 生成唯一文件名
        String fileExtension = originalFilename.substring(lastDotIndex);
        String uniqueFilename = UUID.randomUUID() + fileExtension;
        
        // 存储文件
        Path filePath = uploadPath.resolve(uniqueFilename);
        try {
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace(); 
            return ApiResponse.error(ResultCode.SERVER_ERROR,"文件存储失败");
        }
        
        
        //前端实际访问图片地址
        String lastDir = uploadPath.getFileName().toString();
        String imagePath = "localhost/"+ lastDir + "/" + uniqueFilename;
        // 更新articles表
        Article article = new Article();
        article.setId(articleId);
        article.setCoverImage(imagePath);
        int rowsUpdated = articleMapper.updateArticleSelective(article);

        // 更新images表
        Image image = new Image();
        long bytes = imageFile.getSize();
        long kilobytes = bytes / 1024; // 取整（直接除以 1024，自动舍去小数）
        String contentType = imageFile.getContentType();
        String username = null;
        //获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 获取用户名
            username = authentication.getName();
            // 进行相关操作
            System.out.println("Current user: " + username);
        } else {
           
            System.out.println("No user is authenticated");
        }
        image.setFilePath(imagePath);
        image.setFileName(uniqueFilename);
        image.setFileSize(kilobytes);
        image.setMimeType(contentType);
        image.setCreatedBy(username);
        int rowsUpdated1 = imageMapper.insertImage(image);
        Long imageId = imageMapper.getImageIdByfilePath(imagePath);
        
        //更新articles_images表
        ArticleImage articleImage = new ArticleImage();
        articleImage.setArticleId(articleId);
        articleImage.setImageId(imageId);
        int rowsUpdated2 = imageMapper.insertArticleImage(articleImage);

        return rowsUpdated > 0 && rowsUpdated1 > 0 && rowsUpdated2 > 0 
            ? ApiResponse.success(imagePath)
            : ApiResponse.error(ResultCode.SERVER_ERROR);
    }

    @Override
    public ApiResponse<String> addCategory(CategoryRequest request) {
        String categoryName = request.getCategoryName();
        Long articleId = request.getArticleId();
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        Article article = articleMapper.getArticleById(articleId);
        if ( article == null ) {
            return ApiResponse.error(ResultCode.PARAM_ERROR,"文章不存在");
        }
        boolean flag = categoryMapper.checkExactName(categoryName);
        if ( !flag ) {
            int rowsInserted = categoryMapper.createCategory(newCategory);
            Long newCategoryId = newCategory.getId();
            ArticleCategory articleCategory = new ArticleCategory();
            articleCategory.setArticleId(articleId);
            articleCategory.setCategoryId(newCategoryId);
            int rowsInserted1 = categoryMapper.insertCategoryReflect(articleCategory);
            return rowsInserted1 > 0  && rowsInserted > 0
                ? ApiResponse.success()
                : ApiResponse.error(ResultCode.SERVER_ERROR,"文章分类设置失败");
        }
        return ApiResponse.error(ResultCode.PARAM_ERROR,"文章分类已存在");
    }

    @Override
    public ApiResponse<String> addTag(TagRequest request) {
        List<String> tagNames = request.getTagNames();
        Long articleId = request.getArticleId();
        Article article = articleMapper.getArticleById(articleId);
        if ( article == null ) {
            return ApiResponse.error(ResultCode.PARAM_ERROR,"文章不存在");
        }
        int tagNamesLen = 0;
        int rowsInserted = 0;
        int rowsInserted1 = 0;
        for ( String tagName: tagNames ) {
            boolean flag = tagMapper.checkExactName(tagName);
            if ( flag ) {
                continue;
            } else {
                tagNamesLen += 1;
                Tag tag = new Tag();
                tag.setName(tagName);
                rowsInserted += tagMapper.createTag(tag);
                Long tagId = tag.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagId);
                rowsInserted1 += tagMapper.insertArticleTag(articleTag);
            }
        }
        return rowsInserted >= tagNamesLen && rowsInserted1 >= tagNamesLen
            ? ApiResponse.success()
            : ApiResponse.error(ResultCode.SERVER_ERROR,"文章标签设置失败");
    }

    @Override
    public Long countAllArticles(){
        Long articleCount = articleMapper.countAllArticles();
        return articleCount;
    }


    @Override
    public ApiResponse<ArticleRecommendResponse> getRecommendArticleById(Long id) {
        List<ArticleVO> articleVOs = articleMapper.getRecommendedArticles(id);
        List<ArticleDetailResponse> recommend = ArticleConvert.convertToDetailResponseList(articleVOs);

        
        ArticleVO prev = articleMapper.getPrevArticle(id);
        ArticleVO next = articleMapper.getNextArticle(id);
        // convertToDetailResponse 已经做了null 判空保护
        ArticleDetailResponse prevCt = ArticleConvert.convertToDetailResponse(prev); 
        ArticleDetailResponse nextCt = ArticleConvert.convertToDetailResponse(next);
        // prev 和 next 为 null的情况下该怎么处理
        ArticleVO articleVO = articleMapper.getArticlePageFormById(id);
        ArticleDetailResponse articleVOCt = ArticleConvert.convertToDetailResponse(articleVO);
        if (prevCt == null) {
            prevCt = articleVOCt;
        }
        if (nextCt == null) {
            nextCt = articleVOCt;
        }

        ArticleDetailResponsePair articleDetailResponsePair = new ArticleDetailResponsePair();
        articleDetailResponsePair.setPrevious(prevCt);
        articleDetailResponsePair.setNext(nextCt);

        ArticleRecommendResponse articleRecommendResponse = new ArticleRecommendResponse();
        articleRecommendResponse.setPrevious(articleDetailResponsePair.getPrevious());
        articleRecommendResponse.setNext(articleDetailResponsePair.getNext());
        articleRecommendResponse.setRecommend(recommend);

        return ApiResponse.success(articleRecommendResponse);
    }

    @Override
    public ApiResponse<Long> getLikesById(Long id) {
        Long likes = articleMapper.getLikesById(id);
        return ApiResponse.success(likes);
    }

    @Override
    public ApiResponse<TimeLineResponse> getTimeLine(TimeLineRequest request) {
        // 参数校验
        int pageNum = request.current() != null ? request.current() : 1;
        int pageSize = request.size() != null ? request.size() : 10;
        
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<ArticleTimeLineResponse> list = articleMapper.selectArticleOrderedByCreatedAt();
        // 获取分页信息
        PageInfo<ArticleTimeLineResponse> pageInfo = new PageInfo<>(list);
        
        // 封装返回结果
        TimeLineResponse response = new TimeLineResponse();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setList(pageInfo.getList());      // 当前页数据
        return ApiResponse.success(response);
    }
}
