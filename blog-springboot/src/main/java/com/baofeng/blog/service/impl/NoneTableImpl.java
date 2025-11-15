package com.baofeng.blog.service.impl;



import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.client.RestTemplate;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.OneSentenceDTO;
import com.baofeng.blog.service.NoneTableService;


@Service

@RequiredArgsConstructor // 替代@Autowerid属性注入
public class NoneTableImpl implements NoneTableService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(NoneTableService.class);
    private final List<String> sentenceApi = Arrays.asList(
            "https://v1.hitokoto.cn/",
            "https://v2.jinrishici.com/one.json"
    );
    @Override
    public ApiResponse<String> getOneSentence() {
     // 随机挑选一个 API
        String api = sentenceApi.get(new Random().nextInt(sentenceApi.size()));

        try {
            if (api.contains("hitokoto")) {
                OneSentenceDTO.OneSentenceAPI one = restTemplate.getForObject(api, OneSentenceDTO.OneSentenceAPI.class);
                if (one != null) {
                    return ApiResponse.success(one.getHitokoto());
                }
            } else if (api.contains("jinrishici")) {
                OneSentenceDTO.PoetryToday poetry = restTemplate.getForObject(api, OneSentenceDTO.PoetryToday.class);
                if (poetry != null && poetry.getStatus().equals("success")) {
                    return ApiResponse.success(poetry.getData().getContent());
                }
            }
        } catch (Exception e) {
            logger.error("获取句子失败", e);
        }
        return ApiResponse.success("今天也要元气满满");
    }
}
