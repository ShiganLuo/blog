package com.baofeng.blog.service.impl;

import com.baofeng.blog.vo.admin.AdminTagPageVO.TagPageRequestVO;
import com.baofeng.blog.vo.admin.AdminTagPageVO.TagPageResponseVO;
import com.baofeng.blog.vo.admin.AdminTagPageVO.TagVO;
import com.baofeng.blog.vo.front.FrontTagVO;
import com.baofeng.blog.vo.admin.AdminTagPageVO.CreateTagRequest;
import com.baofeng.blog.vo.admin.AdminTagPageVO.TagDictionaryResponse;
import com.baofeng.blog.entity.Tag;
import com.baofeng.blog.mapper.TagMapper;
import com.baofeng.blog.service.TagService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagMapper tagMapper;

    @Override
    public TagPageResponseVO getTagPage(TagPageRequestVO request) {
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
        return response;
    }

    @Override
    public boolean createTag(CreateTagRequest request) {
        // 检查标签名称是否已存在
        if (tagMapper.getTagByName(request.name()) != null) {
            throw new RuntimeException("标签名称已存在");
        }

        // 创建标签
        Tag tag = new Tag();
        tag.setName(request.name());

        // 保存标签
        return tagMapper.createTag(tag) > 0;
    }

    @Override
    public boolean deleteTag(Long id) {
        // 检查标签是否存在
        Tag tag = tagMapper.getTagById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }

        // 检查标签下是否有文章
        int articleCount = tagMapper.getArticleCount(id);
        if (articleCount > 0) {
            throw new RuntimeException("该标签下还有" + articleCount + "篇文章，无法删除");
        }

        // 删除标签
        return tagMapper.deleteTag(id) > 0;
    }
    @Override
    public List<Tag> getTagDictionary(){
        List<Tag> tags = tagMapper.getAllTags();
        return tags;
    }

    @Override
    public Long countAllTags(){
        Long tagCount = tagMapper.countAllTags();
        return tagCount;
    }

    @Override
    public List<FrontTagVO> getHotTags(int limit) {
        // 限制最大返回数量为20
        int maxLimit = Math.min(limit, 20);
        return tagMapper.getHotTags(maxLimit);
    }

    @Override
    public Tag getTagDetail(Long id) {
        return tagMapper.getTagById(id);
    }
} 