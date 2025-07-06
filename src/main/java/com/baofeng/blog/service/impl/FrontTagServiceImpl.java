package com.baofeng.blog.service.impl;

import com.baofeng.blog.vo.front.FrontTagVO;
import com.baofeng.blog.mapper.FrontTagMapper;
import com.baofeng.blog.service.FrontTagService;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FrontTagServiceImpl implements FrontTagService {
    private final FrontTagMapper frontTagMapper;

    @Override
    public List<FrontTagVO> getAllTags() {
        return frontTagMapper.getAllTags();
    }

    @Override
    public List<FrontTagVO> getHotTags(int limit) {
        // 限制最大返回数量为20
        int maxLimit = Math.min(limit, 20);
        return frontTagMapper.getHotTags(maxLimit);
    }

    @Override
    public FrontTagVO.TagDetailVO getTagDetail(Long id) {
        return frontTagMapper.getTagDetail(id);
    }
} 