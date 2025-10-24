package com.baofeng.blog.service.impl;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminArticleVO.*;
import com.baofeng.blog.vo.common.Image.UploadImage;
import com.baofeng.blog.vo.front.FrontArticleVO.*;
import com.baofeng.blog.util.ArticleConvert;
import com.baofeng.blog.entity.*;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.mapper.*;
import com.baofeng.blog.util.ImageFileUtil;
import com.baofeng.blog.service.ArticleService;
import com.baofeng.blog.service.MinioService;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;



@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;
    private final ImageMapper imageMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final EntityImageMapper entityImageMapper;
    private final MinioService minioService;
    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);
    @Value("${app.upload.dir}")
    private String uploadDir;
    @Value("${app.upload.ipPrefix}")
    private String ipPrefix;

    public ArticleServiceImpl(MinioService minioService,
                              ArticleMapper articleMapper,
                              ImageMapper imageMapper,
                              UserMapper userMapper,
                              CategoryMapper categoryMapper,
                              TagMapper tagMapper,
                              EntityImageMapper entityImageMapper) {
        this.minioService = minioService;
        this.articleMapper = articleMapper;
        this.imageMapper = imageMapper;
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.entityImageMapper = entityImageMapper;
    }

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
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"用户不存在");
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
            return ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"文章创建失败");
        }
    }

    @Override
    public ApiResponse<String> deleteArticle(Long id){
        int rowsDeleted = articleMapper.deleteArticle(id);
        return rowsDeleted > 0 
            ? ApiResponse.success()
            : ApiResponse.error(ResultCodeEnum.NOT_FOUND,"文章未找到");
    }

    @Override
    public ApiResponse<Article> getArticleById(Long id){
        Article article = articleMapper.getArticleById(id);
        return article != null 
            ? ApiResponse.success(article)
            : ApiResponse.error(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public ApiResponse<String> updateArticleSelective(Article article){
        int rowsUpdated = articleMapper.updateArticleSelective(article);
        return rowsUpdated > 0 
            ? ApiResponse.success()
            : ApiResponse.error(ResultCodeEnum.BAD_REQUEST);
    }

    @Override
    public ApiResponse<String> updatePinStaus(Long id,boolean isPinned){
        Article article = new Article();
        article.setId(id);
        article.setIsFeatured(isPinned);
        int rowsUpdated = articleMapper.updateArticleSelective(article);

        return rowsUpdated > 0
            ? ApiResponse.success()
            : ApiResponse.error(ResultCodeEnum.BAD_REQUEST);
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
                : ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"文章发布失败");
        } else {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "作者ID与文章实际作者ID不一致");
        }
    }
    @Override
    public ApiResponse<String> isTitleExist(String title){
        boolean isDuplicated = articleMapper.isTitleExist(title);
        return isDuplicated
            ? ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"标题已存在")
            : ApiResponse.success("可以使用该标题");
    }

    /**
     * 存储图片到服务器并返回相对路径
     * @param imageFile 上传的图片文件
     * @return 图片的相对路径
     * @throws IOException 如果存储失败
     */
    @Override
    public ApiResponse<String> storeImage(UploadImage uploadImage) {
        // 1. 检查文章是否存在
        String entityType = uploadImage.entityType();
        Long articleId = uploadImage.entityId();
        MultipartFile imageFile = uploadImage.file();
        String usageType = uploadImage.usageType();
        Article article1 = articleMapper.getArticleById(articleId);
        if (article1 == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "文章不存在");
        }

        String uniqueFilename = ImageFileUtil.generateUniqueImageName(imageFile);
        if (uniqueFilename == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"图片为空或图片类型错误, 目前仅支持jpg、png、gif、bmp");
        }
        try {
            // ===== 使用 MinioService 上传 =====
            minioService.uploadFile(
                    uniqueFilename,
                    imageFile.getInputStream(),
                    imageFile.getSize(),
                    imageFile.getContentType()
            );

            String imagePath = minioService.getPermanentFileUrl(uniqueFilename);

            Article article = new Article();
            article.setId(articleId);
            article.setCoverImage(imagePath);
            int rowsUpdated = articleMapper.updateArticleSelective(article);

            Image image = new Image();
            long bytes = imageFile.getSize();
            long kilobytes = bytes / 1024;
            String contentType = imageFile.getContentType();
            String username = null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                username = authentication.getName();
            }

            image.setFilePath(imagePath);
            image.setFileName(uniqueFilename);
            image.setFileSize(kilobytes);
            image.setMimeType(contentType);
            image.setCreatedBy(username);
            int rowsUpdated1 = imageMapper.insertImage(image);
            Long imageId = imageMapper.getImageIdByfilePath(imagePath);

            EntityImage articleImage = new EntityImage();
            articleImage.setEntityType(entityType);
            articleImage.setEntityId(articleId);
            articleImage.setImageId(imageId);
            articleImage.setUsageType(usageType);
            int rowsUpdated2 = entityImageMapper.insertEntityImage(articleImage);

            return rowsUpdated > 0 && rowsUpdated1 > 0 && rowsUpdated2 > 0
                    ? ApiResponse.success(imagePath)
                    : ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR, "文件存储失败");

        } catch (Exception e) {
            logger.warn("minio存储文件失败",e);
            return ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR, "文件存储失败");
        }
    }


    @Override
    public ApiResponse<String> addCategory(CategoryRequest request) {
        String categoryName = request.getCategoryName();
        Long articleId = request.getArticleId();
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        Article article = articleMapper.getArticleById(articleId);
        if ( article == null ) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"文章不存在");
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
                : ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"文章分类设置失败");
        }
        return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"文章分类已存在");
    }

    @Override
    public ApiResponse<String> addTag(TagRequest request) {
        List<String> tagNames = request.getTagNames();
        Long articleId = request.getArticleId();
        Article article = articleMapper.getArticleById(articleId);
        if ( article == null ) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"文章不存在");
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
            : ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"文章标签设置失败");
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
    public ApiResponse<ArticleAbstractsResponse> getTimeLine(TimeLineRequest request) {
        // 参数校验
        int pageNum = request.current() != null ? request.current() : 1;
        int pageSize = request.size() != null ? request.size() : 10;
        
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<ArticleAbstractResponse> list = articleMapper.selectArticleOrderedByCreatedAt();
        // 获取分页信息
        PageInfo<ArticleAbstractResponse> pageInfo = new PageInfo<>(list);
        
        // 封装返回结果
        ArticleAbstractsResponse response = new ArticleAbstractsResponse();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setList(pageInfo.getList());      // 当前页数据
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<ArticleAbstractsResponse> getArticlesByTagId(CategoryOrTagRequest request) {
        // 参数校验
        int pageNum = request.current() != null ? request.current() : 1;
        int pageSize = request.size() != null ? request.size() : 10;
        
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<ArticleAbstractResponse> list = articleMapper.selectArticlesByTagId(request.id());
        // 获取分页信息
        PageInfo<ArticleAbstractResponse> pageInfo = new PageInfo<>(list);
        
        // 封装返回结果
        ArticleAbstractsResponse response = new ArticleAbstractsResponse();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setList(pageInfo.getList());      // 当前页数据
        return ApiResponse.success(response);
        
    }

    @Override
    public ApiResponse<ArticleAbstractsResponse> getArticlesByCategoryId(CategoryOrTagRequest request) {
        // 参数校验
        int pageNum = request.current() != null ? request.current() : 1;
        int pageSize = request.size() != null ? request.size() : 10; 
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<ArticleAbstractResponse> list = articleMapper.selectArticlesByCategoryId(request.id());
        // 获取分页信息
        PageInfo<ArticleAbstractResponse> pageInfo = new PageInfo<>(list); 
        // 封装返回结果
        ArticleAbstractsResponse response = new ArticleAbstractsResponse();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setList(pageInfo.getList());      // 当前页数据
        return ApiResponse.success(response);
    }
    public ApiResponse<AdminArticlePageVO> getAdminArticleList(CreateAdminArticlePageRequest createAdminArticlePageRequest) {
        int current = createAdminArticlePageRequest.current() != null ? createAdminArticlePageRequest.current() : 1;
        int size = createAdminArticlePageRequest.size() != null ? createAdminArticlePageRequest.size() : 10;
        PageHelper.startPage(current, size);
        List<AdminArticle> adminArticles = articleMapper.getAdminArticlePage(createAdminArticlePageRequest);
        PageInfo<AdminArticle> pageInfo = new PageInfo<>(adminArticles);
        AdminArticlePageVO adminArticlePageVO = new AdminArticlePageVO();
        adminArticlePageVO.setList(pageInfo.getList());
        adminArticlePageVO.setTotal(pageInfo.getTotal());
        return ApiResponse.success(adminArticlePageVO);
    }   
}
