package com.baofeng.blog.service.impl;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.CreateTagRequest;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.TagPageRequestVO;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.TagPageResponseVO;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.TagVO;
import com.baofeng.blog.dto.common.TagDTO.TagDictionaryResponse;
import com.baofeng.blog.entity.Tag;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.mapper.TagMapper;
import com.baofeng.blog.service.TagService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    public TagServiceImpl (
        TagMapper tagMapper
    ) {
        this.tagMapper = tagMapper;
    }


    @Override
    public ApiResponse<TagPageResponseVO> getTagPage(TagPageRequestVO request) {
        // 参数校验
        int pageNum = request.pageNum() != null ? request.pageNum() : 1;
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;
        
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        
        // 执行查询
        List<TagVO> list = tagMapper.getTagPage(request);
        
        // 获取分页信息
        PageInfo<TagVO> pageInfo = new PageInfo<>(list);
        
        // 封装返回结果
        TagPageResponseVO response = new TagPageResponseVO();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setPages(pageInfo.getPages());    // 总页数
        response.setList(pageInfo.getList());      // 当前页数据
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<String> createTag(CreateTagRequest request) {
        // 检查标签名称是否已存在
        if (tagMapper.getTagByName(request.name()) != null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"标签名称已存在");
        }

        // 创建标签
        Tag tag = new Tag();
        tag.setName(request.name());
        int rowUpdated = tagMapper.createTag(tag);
        // 保存标签
        return rowUpdated > 0
            ? ApiResponse.success()
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"标签创建失败");
    }

    @Override
    public ApiResponse<String> deleteTag(Long id) {
        // 检查标签是否存在
        Tag tag = tagMapper.getTagById(id);
        if (tag == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"标签不存在");
        }

        // 检查标签下是否有文章
        int articleCount = tagMapper.getArticleCount(id);
        if (articleCount > 0) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"该标签下还有" + articleCount + "篇文章，无法删除");
        }
        int rowsDeleted = tagMapper.deleteTag(id);
        // 删除标签
        return rowsDeleted > 0
            ? ApiResponse.success()
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"删除失败");
    }
    @Override
    public ApiResponse<List<TagDictionaryResponse>> getTagDictionary(String keyword){
        List<Tag> tags = tagMapper.getTagsByKeyword(keyword);
        List<TagDictionaryResponse> tagDictionaryResponse = tags.stream()
            .map(tag -> {
                TagDictionaryResponse resp = new TagDictionaryResponse();
                resp.setId(tag.getId());
                resp.setTagName(tag.getName());
                return resp;
            })
            .collect(Collectors.toList());
        return ApiResponse.success(tagDictionaryResponse);
    }

    @Override
    public Long countAllTags(){
        Long tagCount = tagMapper.countAllTags();
        return tagCount;
    }

    @Override
    public ApiResponse<List<String>> getHotTags(int limit) {
        // 限制最大返回数量为20
        int maxLimit = Math.min(limit, 20);
        List<String> hotTags = tagMapper.getHotTags(maxLimit);
        return ApiResponse.success(hotTags);
    }

    @Override
    public ApiResponse<Tag> getTagDetail(Long id) {
        Tag tag = tagMapper.getTagById(id);
        return ApiResponse.success(tag);
    }
} 