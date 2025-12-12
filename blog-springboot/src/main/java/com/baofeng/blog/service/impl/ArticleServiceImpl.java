package com.baofeng.blog.service.impl;

import com.baofeng.blog.util.ArticleConvertUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminArticleDTO.*;
import com.baofeng.blog.dto.common.ImageDTO.UploadImage;
import com.baofeng.blog.dto.front.FrontArticleDTO.*;
import com.baofeng.blog.entity.*;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.enums.ArticleStatusEnum;
import com.baofeng.blog.mapper.*;
import com.baofeng.blog.util.ImageFileUtil;
import com.baofeng.blog.service.ArticleService;
import com.baofeng.blog.service.MinioService;
import com.baofeng.blog.util.ListDiffUtil;

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
import java.util.Map;



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
        article.setStatus(ArticleStatusEnum.PUBLIC.getCode());
        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        int rowsInserted = articleMapper.insertArticle(article);

        if(rowsInserted > 0){
            //插入后自动回填文章id
            Long articleId = article.getId();
            return ApiResponse.success(articleId);
        }else{
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"文章创建失败");
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
    public ApiResponse<String> deleteArticlesLogically(DeleteArticlesLogicallyRequest deleteArticlesLogicallyRequest) {
        for (Long id: deleteArticlesLogicallyRequest.ids()) {
            int rowsUpdated = articleMapper.delteArticleLogically(id, deleteArticlesLogicallyRequest.isDeleted());
            if (rowsUpdated == 0) {
                logger.info("文章" + id + "更新失败");
                return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"文章" + id + "更新失败");
            }
        }
        return ApiResponse.success("文章逻辑删除成功");
    }

    @Override
    public ApiResponse<AdminArticle> getAdminArticleById(Long id){
        AdminArticle adminArticle = articleMapper.getAdminArticleById(id);
        return adminArticle != null 
            ? ApiResponse.success(adminArticle)
            : ApiResponse.error(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public ApiResponse<String> updateArticlesSelective(UpdateArticlesRequest updateArticlesRequest){
        List<Long> ids = updateArticlesRequest.ids(); // 改成 ids

        if ((ids == null || ids.isEmpty()) && updateArticlesRequest.id() != null) {
            Long singleId = updateArticlesRequest.id();            
            ids = List.of(singleId); 
        } else {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"id不能为空");
        }
        // 目录名列表
        List<String> categoryNameList = updateArticlesRequest.categoryNameList();

        // 标签名列表
        List<String> tagNameList = updateArticlesRequest.tagNameList();

        boolean allSuccess = true;

        for (Long articleId : ids) {
            // ---- 分类 ----
            List<String> existCategoryNameList = categoryMapper.getCategoryNamesByArticleId(articleId);
            Map<String, List<String>> categoryDiff = ListDiffUtil.diffList(existCategoryNameList, categoryNameList);
            List<String> categoriesToAdd = categoryDiff.get("toAdd");
            List<String> categoriesToRemove = categoryDiff.get("toRemove");

            for (String categoryName : categoriesToAdd) {
                Long categoryId = categoryMapper.JudgeIsCategoryExistByCategoryName(categoryName);
                if (categoryId == null) {
                    Category category = new Category();
                    category.setName(categoryName);
                    categoryMapper.createCategory(category);
                    categoryId = category.getId();
                }

                ArticleCategory articleCategory = new ArticleCategory();
                articleCategory.setArticleId(articleId);
                articleCategory.setCategoryId(categoryId);
                categoryMapper.insertArticleCategory(articleCategory);
            }

            for (String categoryName : categoriesToRemove) {
                categoryMapper.deleteCategoryByCategoryName(categoryName);
                // 级联删除不需要删除articleCategory
            }

            // ---- 标签 ----
            List<String> existTagNameList = tagMapper.getTagNamesByArticleId(articleId);
            Map<String, List<String>> tagDiff = ListDiffUtil.diffList(existTagNameList, tagNameList);
            List<String> tagsToAdd = tagDiff.get("toAdd");
            List<String> tagsToRemove = tagDiff.get("toRemove");

            for (String tagName : tagsToAdd) {
                Long tagId = tagMapper.JudegIsTagExistByTagName(tagName);
                if (tagId == null) {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    tagMapper.createTag(tag);
                    tagId = tag.getId();
                }

                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagId);
                tagMapper.insertArticleTag(articleTag);
            }

            for (String tagName : tagsToRemove) {
                tagMapper.delteTagByTagName(tagName);
                // 级联删除不需要删除articleTag
            }

            // ---- 更新文章 ----
            Article article = Article.builder()
                .id(articleId)
                .title(updateArticlesRequest.articleTitle())
                .content(updateArticlesRequest.articleContent())
                .coverImage(updateArticlesRequest.articleCover())
                .isFeatured(updateArticlesRequest.isFeatured())
                .isTop(updateArticlesRequest.isTop())
                .originUrl(updateArticlesRequest.originUrl())
                .type(updateArticlesRequest.type())
                .isDeleted(updateArticlesRequest.isDeleted())
                .build();

            int rowsUpdated = articleMapper.updateArticleSelective(article);
            if (rowsUpdated <= 0) {
                allSuccess = false;
            }
        }

        return allSuccess 
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
    public ApiResponse<FrontArticlePageResponse> getArticlePage(ArticlePageRequest articlePageRequest) {
        // 参数校验
        int pageNum = articlePageRequest.pageNum() != null ? articlePageRequest.pageNum() : 1;
        int pageSize = articlePageRequest.pageSize() != null ? articlePageRequest.pageSize() : 10;
        
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<FrontArticle> list = articleMapper.getFrontArticles(articlePageRequest);
        // 获取分页信息
        PageInfo<FrontArticle> pageInfo = new PageInfo<>(list);
        
        // 封装返回结果
        FrontArticlePageResponse response = new FrontArticlePageResponse();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setPages(pageInfo.getPages());    // 总页数
        response.setList(pageInfo.getList());      // 当前页数据
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<ArticleDetailResponse> getArticlePageFormById(Long id) {
        FrontArticle frontArticle = articleMapper.getFrontArticleById(id);
        ArticleDetailResponse articleDetailResponse = ArticleConvertUtil.convertToDetailResponse(frontArticle);
        return ApiResponse.success(articleDetailResponse);
    }

    @Override
    public ApiResponse<String> publishArticle(Long articleId,Long authorId) {
        Long articleAuthorId = articleMapper.getAuthorIdById(articleId);
        if ( articleAuthorId == authorId ) {
            Article article = new Article();
            article.setId(articleId);
            article.setStatus(ArticleStatusEnum.PUBLIC.getCode());
            article.setPublishedAt(LocalDateTime.now());
            int rowsUpdated = articleMapper.updateArticleSelective(article);
            return rowsUpdated > 0
                ? ApiResponse.success()
                : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"文章发布失败");
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
                    : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "文件存储失败");

        } catch (Exception e) {
            logger.warn("minio存储文件失败",e);
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "文件存储失败");
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
            Category category = new Category();
            category.setName(categoryName);
            categoryMapper.createCategory(category);
            Long categoryId = category.getId();
            ArticleCategory articleCategory = new ArticleCategory();
            articleCategory.setArticleId(articleId);
            articleCategory.setCategoryId(categoryId);
            int rowsInserted1 = categoryMapper.insertArticleCategory(articleCategory);
            return rowsInserted1 > 0  && category != null
                ? ApiResponse.success()
                : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"文章分类设置失败");
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
                tagMapper.createTag(tag);
                rowsInserted += 1;
                Long tagId = tag.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagId);
                rowsInserted1 += tagMapper.insertArticleTag(articleTag);
            }
        }
        return rowsInserted >= tagNamesLen && rowsInserted1 >= tagNamesLen
            ? ApiResponse.success()
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"文章标签设置失败");
    }

    @Override
    public Long countAllArticles(){
        Long articleCount = articleMapper.countAllArticles();
        return articleCount;
    }


    @Override
    public ApiResponse<ArticleRecommendResponse> getRecommendArticleById(Long id) {
        List<FrontArticle> articleVOs = articleMapper.getRecommendedArticles(id);
        List<ArticleDetailResponse> recommend = ArticleConvertUtil.convertToDetailResponseList(articleVOs);

        
        FrontArticle prev = articleMapper.getPrevArticle(id);
        FrontArticle next = articleMapper.getNextArticle(id);
        // convertToDetailResponse 已经做了null 判空保护
        ArticleDetailResponse prevCt = ArticleConvertUtil.convertToDetailResponse(prev); 
        ArticleDetailResponse nextCt = ArticleConvertUtil.convertToDetailResponse(next);
        // prev 和 next 为 null的情况下该怎么处理
        FrontArticle frontArticle = articleMapper.getFrontArticleById(id);
        ArticleDetailResponse articleVOCt = ArticleConvertUtil.convertToDetailResponse(frontArticle);
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
